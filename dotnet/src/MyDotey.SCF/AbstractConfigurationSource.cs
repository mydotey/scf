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
    public abstract class AbstractConfigurationSource<C> : ConfigurationSource
        where C : ConfigurationSourceConfig
    {
        private static readonly Logger LOGGER = LogManager.GetCurrentClassLogger(typeof(AbstractConfigurationSource<>));

        private C _config;

        private volatile List<Action<ConfigurationSourceChangeEvent>> _changeListeners;

        public AbstractConfigurationSource(C config)
        {
            if (config == null)
                throw new ArgumentNullException("config is null");

            _config = config;
        }

        ConfigurationSourceConfig ConfigurationSource.getConfig()
        {
            return getConfig();
        }

        public virtual C getConfig()
        {
            return _config;
        }

        public virtual void addChangeListener(Action<ConfigurationSourceChangeEvent> changeListener)
        {
            if (changeListener == null)
                throw new ArgumentNullException("changeListener is null");

            lock (this)
            {
                if (_changeListeners == null)
                    _changeListeners = new List<Action<ConfigurationSourceChangeEvent>>();

                _changeListeners.Add(changeListener);
            }
        }

        protected virtual void raiseChangeEvent()
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

        public virtual V getPropertyValue<K, V>(PropertyConfig<K, V> propertyConfig)
        {
            object value = getPropertyValue(propertyConfig.getKey());
            return convert(propertyConfig, value);
        }

        protected abstract object getPropertyValue(object key);

        protected virtual V convert<K, V>(PropertyConfig<K, V> propertyConfig, object value)
        {
            if (isNull<V>(value))
                return default(V);

            if (value is V)
                return (V)value;

            if (propertyConfig.getValueConverters() == null)
                return default(V);

            foreach (TypeConverter typeConverter in propertyConfig.getValueConverters())
            {
                if (typeConverter.SourceType.IsAssignableFrom(value.GetType())
                        && typeof(V).IsAssignableFrom(typeConverter.TargetType))
                    return (V)typeConverter.Convert(value);
            }

            return default(V);
        }

        protected virtual bool isNull<V>(object value)
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
            return string.Format("{0} {{ config: {1}, changeListeners: [ {2} ] }}", GetType().Name, getConfig(),
                    _changeListeners == null ? null : string.Join(", ", _changeListeners));
        }
    }
}