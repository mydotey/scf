using System;
using System.Collections.Generic;
using System.Threading;

using NLog;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 16, 2018
     */
    public class DefaultProperty<K, V> : Property<K, V>
    {
        private static Logger LOGGER = LogManager.GetCurrentClassLogger(typeof(DefaultProperty<,>));

        private PropertyConfig<K, V> _config;
        private volatile object _value;
        private volatile List<Action<PropertyChangeEvent<K, V>>> _changeListeners;

        public DefaultProperty(PropertyConfig<K, V> config, V value)
        {
            if (config == null)
                throw new ArgumentNullException("config is null");

            _config = config;
            _value = value;
        }

        PropertyConfig Property.getConfig()
        {
            return getConfig();
        }

        public virtual PropertyConfig<K, V> getConfig()
        {
            return _config;
        }

        object Property.getValue()
        {
            return getValue();
        }

        public virtual V getValue()
        {
            return (V)_value;
        }

        protected internal virtual void setValue(object value)
        {
            _value = (V)value;
        }

        public virtual void addChangeListener(Action<PropertyChangeEvent<K, V>> changeListener)
        {
            if (changeListener == null)
                throw new ArgumentNullException("changeListener", "changeListener is null");

            lock (this)
            {
                if (_changeListeners == null)
                    _changeListeners = new List<Action<PropertyChangeEvent<K, V>>>();
                _changeListeners.Add(changeListener);
            }
        }

        protected virtual void raiseChangeEvent(PropertyChangeEvent<K, V> @event)
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
            return string.Format("{0} {{ config: {1}, value: {2}, changeListeners: [ {3} ] }}", GetType().Name,
                _config, _value, _changeListeners == null ? null : string.Join(", ", _changeListeners));
        }
    }
}