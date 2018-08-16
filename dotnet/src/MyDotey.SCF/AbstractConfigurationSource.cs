using System;
using System.Collections;
using System.Collections.Generic;

using NLog;
using MyDotey.SCF.Type;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 16, 2018
     */
    public abstract class AbstractConfigurationSource<C> : IConfigurationSource
        where C : ConfigurationSourceConfig
    {
        private static readonly Logger LOGGER = LogManager.GetCurrentClassLogger(typeof(AbstractConfigurationSource<>));

        private C _config;

        private volatile List<Action<IConfigurationSourceChangeEvent>> _changeListeners;

        public AbstractConfigurationSource(C config)
        {
            if (config == null)
                throw new ArgumentNullException("config is null");

            _config = config;
        }

        ConfigurationSourceConfig IConfigurationSource.Config { get { return Config; } }

        public virtual C Config { get { return _config; } } 

        public virtual void AddChangeListener(Action<IConfigurationSourceChangeEvent> changeListener)
        {
            if (changeListener == null)
                throw new ArgumentNullException("changeListener is null");

            lock (this)
            {
                if (_changeListeners == null)
                    _changeListeners = new List<Action<IConfigurationSourceChangeEvent>>();

                _changeListeners.Add(changeListener);
            }
        }

        protected virtual void RaiseChangeEvent()
        {
            lock (this)
            {
                if (_changeListeners == null)
                    return;

                _changeListeners.ForEach(l =>
                {
                    try
                    {
                        l(new DefaultConfigurationSourceChangeEvent(this));
                    }
                    catch (Exception e)
                    {
                        LOGGER.Error(e, "source change listener failed to run");
                    }
                });
            }
        }

        public virtual V GetPropertyValue<K, V>(PropertyConfig<K, V> propertyConfig)
        {
            object value = GetPropertyValue(propertyConfig.Key);
            return Convert(propertyConfig, value);
        }

        protected abstract object GetPropertyValue(object key);

        protected virtual V Convert<K, V>(PropertyConfig<K, V> propertyConfig, object value)
        {
            if (IsNull<V>(value))
                return default(V);

            if (value is V)
                return (V)value;

            if (propertyConfig.ValueConverters == null)
                return default(V);

            foreach (ITypeConverter typeConverter in propertyConfig.ValueConverters)
            {
                if (typeConverter.SourceType.IsAssignableFrom(value.GetType())
                        && typeof(V).IsAssignableFrom(typeConverter.TargetType))
                    return (V)typeConverter.Convert(value);
            }

            return default(V);
        }

        protected virtual bool IsNull<V>(object value)
        {
            if (Object.Equals(value, default(V)))
                return true;

            if (value is string)
                return string.IsNullOrWhiteSpace((string)value);

            if (value is ICollection)
                return ((ICollection)value).Count == 0;

            if (value is Array)
                return ((Array)value).Length == 0;

            return false;
        }

        public override string ToString()
        {
            return string.Format("{0} {{ config: {1}, changeListeners: [ {2} ] }}", GetType().Name, Config,
                    _changeListeners == null ? null : string.Join(", ", _changeListeners));
        }
    }
}