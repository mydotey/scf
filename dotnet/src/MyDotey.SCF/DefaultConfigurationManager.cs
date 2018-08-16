using System;
using System.Collections.Generic;
using System.Collections.Concurrent;
using System.Linq;
using sType = System.Type;
using System.Reflection;

using NLog;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 16, 2018
     */
    public class DefaultConfigurationManager : IConfigurationManager
    {
        private static Logger Logger = LogManager.GetCurrentClassLogger(typeof(DefaultConfigurationManager));

        protected class PriorityComparer : IComparer<int>
        {
            public virtual int Compare(int s1, int s2)
            {
                return s1 > s2 ? -1 : (s1 == s2 ? 0 : 1);
            }
        }

        protected static readonly IComparer<int> PRIORITY_COMPARATOR = new PriorityComparer();

        private ConfigurationManagerConfig _config;
        private SortedDictionary<int, IConfigurationSource> _sortedSources;

        private ConcurrentDictionary<object, IProperty> _properties;
        private object _propertiesLock;

        private volatile List<Action<IPropertyChangeEvent>> _changeListeners;

        private MethodInfo _genericGetPropertyValueMethod;

        private sType _defaultPropertyChangeEventType = typeof(DefaultPropertyChangeEvent<,>);

        public DefaultConfigurationManager(ConfigurationManagerConfig config)
        {
            if (config == null)
                throw new ArgumentNullException("config is null");

            _config = config;

            _sortedSources = new SortedDictionary<int, IConfigurationSource>(_config.Sources, PRIORITY_COMPARATOR);
            _sortedSources.Values.ToList().ForEach(s => s.AddChangeListener(OnSourceChange));

            _properties = new ConcurrentDictionary<object, IProperty>();
            _propertiesLock = new object();

            _genericGetPropertyValueMethod = GetType().GetMethods().Where(methodInfo =>
                methodInfo.Name == "GetPropertyValue"
                && methodInfo.IsGenericMethod && methodInfo.GetGenericArguments().Length == 2
                && methodInfo.GetParameters().Count() == 1
            ).Single();

            Logger.Info("Configuration Manager created: {0}", ToString());
        }

        public virtual ConfigurationManagerConfig Config { get { return _config; } }

        public virtual ICollection<IProperty> Properties { get { return _properties.Values.ToList(); } }

        protected virtual SortedDictionary<int, IConfigurationSource> SortedSources { get { return _sortedSources; } }

        public virtual IProperty<K, V> GetProperty<K, V>(PropertyConfig<K, V> propertyConfig)
        {
            if (propertyConfig == null)
                throw new ArgumentNullException("propertyConfig is null");

            _properties.TryGetValue(propertyConfig.Key, out IProperty property);
            if (property == null)
            {
                lock (_propertiesLock)
                {
                    _properties.TryGetValue(propertyConfig.Key, out property);
                    if (property == null)
                    {
                        V value = GetPropertyValue(propertyConfig);
                        property = NewProperty(propertyConfig, value);
                        _properties[propertyConfig.Key] = property;
                    }
                }
            }

            if (!Object.Equals(property.Config, propertyConfig))
                throw new ArgumentException(string.Format(
                        "make sure using same config for property: {0}, previous config: {1}, current Config: {2}",
                        propertyConfig.Key, property.Config, propertyConfig));

            return (IProperty<K, V>)property;
        }

        public virtual V GetPropertyValue<K, V>(PropertyConfig<K, V> propertyConfig)
        {
            if (propertyConfig == null)
                throw new ArgumentNullException("propertyConfig is null");

            foreach (IConfigurationSource source in _sortedSources.Values)
            {
                V value = GetPropertyValue(source, propertyConfig);

                value = ApplyValueFilter(propertyConfig, value);

                if (value != null)
                    return value;
            }

            return propertyConfig.DefaultValue;
        }

        protected virtual V GetPropertyValue<K, V>(IConfigurationSource source, PropertyConfig<K, V> propertyConfig)
        {
            V value = default(V);
            try
            {
                value = source.GetPropertyValue(propertyConfig);
            }
            catch (Exception e)
            {
                string message = string.Format(
                        "error occurred when getting property value, ignore the source. source: {0}, propertyConfig: {1}",
                        source, propertyConfig);
                Logger.Error(e, message);
            }

            return value;
        }

        protected virtual V ApplyValueFilter<K, V>(PropertyConfig<K, V> propertyConfig, V value)
        {
            if (Object.Equals(value, default(V)))
                return value;

            if (propertyConfig.ValueFilter == null)
                return value;

            try
            {
                value = propertyConfig.ValueFilter.Filter(value);
            }
            catch (Exception e)
            {
                string message = string.Format(
                        "failed to run valueFilter, ignore the filter. value: {0}, valueFilter: {1}, propertyConfig: {2}",
                        value, propertyConfig.ValueFilter, propertyConfig);
                Logger.Error(e, message);
            }

            return value;
        }

        protected virtual DefaultProperty<K, V> NewProperty<K, V>(PropertyConfig<K, V> config, V value)
        {
            return new DefaultProperty<K, V>(config, value);
        }

        protected virtual void OnSourceChange(IConfigurationSourceChangeEvent sourceEvent)
        {
            lock (_propertiesLock)
            {
                foreach (IProperty p in _properties.Values)
                {
                    object oldValue = p.Value;
                    object newValue = GetPropertyValue(p.Config);
                    if (Object.Equals(oldValue, newValue))
                        continue;
                    SetPropertyValue(p, newValue);

                    IPropertyChangeEvent @event = NewPropertyChangeEvent(p, oldValue, newValue);
                    _config.TaskExecutor(() => RaiseChangeEvent(p, @event));
                    _config.TaskExecutor(() => RaiseChangeEvent(@event));
                }
            }
        }

        public virtual void AddChangeListener(Action<IPropertyChangeEvent> changeListener)
        {
            if (changeListener == null)
                throw new ArgumentNullException("changeListener is null");

            lock (this)
            {
                if (_changeListeners == null)
                    _changeListeners = new List<Action<IPropertyChangeEvent>>();
                _changeListeners.Add(changeListener);
            }
        }

        protected virtual void RaiseChangeEvent(IPropertyChangeEvent @event)
        {
            if (_changeListeners == null)
                return;

            lock (this)
            {
                _changeListeners.ForEach(l =>
                {
                    try
                    {
                        l(@event);
                    }
                    catch (Exception e)
                    {
                        Logger.Error(e, "property change listener failed to run");
                    }
                });
            }
        }

        public override string ToString()
        {
            return string.Format("{0} {{ config: {1}, properties: {{ {2} }}, changeListeners: [ {3} ] }}", GetType().Name,
                    _config, string.Join(", ", _properties.Select(p => p.Key + ": " + p.Value).ToList()),
                    _changeListeners == null ? null : string.Join(", ", _changeListeners));
        }

        protected virtual object GetPropertyValue(IPropertyConfig propertyConfig)
        {
            return _genericGetPropertyValueMethod
                .MakeGenericMethod(propertyConfig.Key.GetType(), propertyConfig.ValueType)
                .Invoke(this, new object[] { propertyConfig });
        }

        protected virtual void SetPropertyValue(IProperty property, object value)
        {
            MethodInfo setPropertyMethod = property.GetType()
                .GetMethod("SetValue", BindingFlags.Instance | BindingFlags.NonPublic);
            setPropertyMethod.Invoke(property, new object[] { value });
        }

        protected virtual IPropertyChangeEvent NewPropertyChangeEvent(IProperty property, object oldValue, object newValue)
        {
            sType realType = _defaultPropertyChangeEventType
                .MakeGenericType(property.Config.Key.GetType(), property.Config.ValueType);
            return (IPropertyChangeEvent)Activator.CreateInstance(realType, property, oldValue, newValue);
        }

        protected virtual void RaiseChangeEvent(IProperty property, IPropertyChangeEvent @event)
        {
            MethodInfo raiseChangeEventMethod = property.GetType()
                .GetMethod("RaiseChangeEvent", BindingFlags.Instance | BindingFlags.NonPublic);
            raiseChangeEventMethod.Invoke(property, new object[] { @event });
        }
    }
}