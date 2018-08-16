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
    public class DefaultConfigurationManager : ConfigurationManager
    {
        private static Logger LOGGER = LogManager.GetCurrentClassLogger(typeof(DefaultConfigurationManager));

        protected class PriorityComparer : IComparer<int>
        {
            public virtual int Compare(int s1, int s2)
            {
                return s1 > s2 ? -1 : (s1 == s2 ? 0 : 1);
            }
        }

        protected static readonly IComparer<int> PRIORITY_COMPARATOR = new PriorityComparer();

        private ConfigurationManagerConfig _config;
        private SortedDictionary<int, ConfigurationSource> _sortedSources;

        private ConcurrentDictionary<object, Property> _properties;
        private object _propertiesLock;

        private volatile List<Action<PropertyChangeEvent>> _changeListeners;

        private MethodInfo _genericGetPropertyValueMethod;

        private sType _defaultPropertyChangeEventType = typeof(DefaultPropertyChangeEvent<,>);

        public DefaultConfigurationManager(ConfigurationManagerConfig config)
        {
            if (config == null)
                throw new ArgumentNullException("config is null");

            _config = config;

            _sortedSources = new SortedDictionary<int, ConfigurationSource>(_config.getSources(), PRIORITY_COMPARATOR);
            _sortedSources.Values.ToList().ForEach(s => s.addChangeListener(onSourceChange));

            _properties = new ConcurrentDictionary<object, Property>();
            _propertiesLock = new object();

            _genericGetPropertyValueMethod = GetType().GetMethods().Where(methodInfo =>
                methodInfo.Name == "getPropertyValue"
                && methodInfo.IsGenericMethod && methodInfo.GetGenericArguments().Length == 2
                && methodInfo.GetParameters().Count() == 1
            ).Single();

            LOGGER.Info("Configuration Manager created: {0}", ToString());
        }

        public virtual ConfigurationManagerConfig getConfig()
        {
            return _config;
        }

        public virtual ICollection<Property> getProperties()
        {
            return _properties.Values.ToList();
        }

        protected virtual SortedDictionary<int, ConfigurationSource> getSortedSources()
        {
            return _sortedSources;
        }

        public virtual Property<K, V> getProperty<K, V>(PropertyConfig<K, V> propertyConfig)
        {
            if (propertyConfig == null)
                throw new ArgumentNullException("propertyConfig is null");

            _properties.TryGetValue(propertyConfig.getKey(), out Property property);
            if (property == null)
            {
                lock (_propertiesLock)
                {
                    _properties.TryGetValue(propertyConfig.getKey(), out property);
                    if (property == null)
                    {
                        V value = getPropertyValue(propertyConfig);
                        property = newProperty(propertyConfig, value);
                        _properties[propertyConfig.getKey()] = property;
                    }
                }
            }

            if (!Object.Equals(property.getConfig(), propertyConfig))
                throw new ArgumentException(string.Format(
                        "make sure using same config for property: {0}, previous config: {1}, current Config: {2}",
                        propertyConfig.getKey(), property.getConfig(), propertyConfig));

            return (Property<K, V>)property;
        }

        public virtual V getPropertyValue<K, V>(PropertyConfig<K, V> propertyConfig)
        {
            if (propertyConfig == null)
                throw new ArgumentNullException("propertyConfig is null");

            foreach (ConfigurationSource source in _sortedSources.Values)
            {
                V value = getPropertyValue(source, propertyConfig);

                value = applyValueFilter(propertyConfig, value);

                if (value != null)
                    return value;
            }

            return propertyConfig.getDefaultValue();
        }

        protected virtual V getPropertyValue<K, V>(ConfigurationSource source, PropertyConfig<K, V> propertyConfig)
        {
            V value = default(V);
            try
            {
                value = source.getPropertyValue(propertyConfig);
            }
            catch (Exception e)
            {
                string message = string.Format(
                        "error occurred when getting property value, ignore the source. source: {0}, propertyConfig: {1}",
                        source, propertyConfig);
                LOGGER.Error(e, message);
            }

            return value;
        }

        protected virtual V applyValueFilter<K, V>(PropertyConfig<K, V> propertyConfig, V value)
        {
            if (Object.Equals(value, default(V)))
                return value;

            if (propertyConfig.getValueFilter() == null)
                return value;

            try
            {
                value = propertyConfig.getValueFilter().Filter(value);
            }
            catch (Exception e)
            {
                string message = string.Format(
                        "failed to run valueFilter, ignore the filter. value: {0}, valueFilter: {1}, propertyConfig: {2}",
                        value, propertyConfig.getValueFilter(), propertyConfig);
                LOGGER.Error(e, message);
            }

            return value;
        }

        protected virtual DefaultProperty<K, V> newProperty<K, V>(PropertyConfig<K, V> config, V value)
        {
            return new DefaultProperty<K, V>(config, value);
        }

        protected virtual void onSourceChange(ConfigurationSourceChangeEvent sourceEvent)
        {
            lock (_propertiesLock)
            {
                foreach (Property p in _properties.Values)
                {
                    object oldValue = p.getValue();
                    object newValue = getPropertyValue(p.getConfig());
                    if (Object.Equals(oldValue, newValue))
                        continue;
                    setPropertyValue(p, newValue);

                    PropertyChangeEvent @event = newPropertyChangeEvent(p, oldValue, newValue);
                    _config.getTaskExecutor()(() => raiseChangeEvent(p, @event));
                    _config.getTaskExecutor()(() => raiseChangeEvent(@event));
                }
            }
        }

        public virtual void addChangeListener(Action<PropertyChangeEvent> changeListener)
        {
            if (changeListener == null)
                throw new ArgumentNullException("changeListener is null");

            lock (this)
            {
                if (_changeListeners == null)
                    _changeListeners = new List<Action<PropertyChangeEvent>>();
                _changeListeners.Add(changeListener);
            }
        }

        protected virtual void raiseChangeEvent(PropertyChangeEvent @event)
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
                        LOGGER.Error(e, "property change listener failed to run");
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

        protected virtual object getPropertyValue(PropertyConfig propertyConfig)
        {
            return _genericGetPropertyValueMethod
                .MakeGenericMethod(propertyConfig.getKey().GetType(), propertyConfig.getValueType())
                .Invoke(this, new object[] { propertyConfig });
        }

        protected virtual void setPropertyValue(Property property, object value)
        {
            MethodInfo setPropertyMethod = property.GetType()
                .GetMethod("setValue", BindingFlags.Instance | BindingFlags.NonPublic);
            setPropertyMethod.Invoke(property, new object[] { value });
        }

        protected virtual PropertyChangeEvent newPropertyChangeEvent(Property property, object oldValue, object newValue)
        {
            sType realType = _defaultPropertyChangeEventType
                .MakeGenericType(property.getConfig().getKey().GetType(), property.getConfig().getValueType());
            return (PropertyChangeEvent)Activator.CreateInstance(realType, property, oldValue, newValue);
        }

        protected virtual void raiseChangeEvent(Property property, PropertyChangeEvent @event)
        {
            MethodInfo raiseChangeEventMethod = property.GetType()
                .GetMethod("raiseChangeEvent", BindingFlags.Instance | BindingFlags.NonPublic);
            raiseChangeEventMethod.Invoke(property, new object[] { @event });
        }
    }
}