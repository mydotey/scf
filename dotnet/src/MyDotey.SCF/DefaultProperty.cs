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
    public class DefaultProperty<K, V> : IProperty<K, V>
    {
        private static Logger Logger = LogManager.GetCurrentClassLogger(typeof(DefaultProperty<,>));

        private PropertyConfig<K, V> _config;
        private volatile object _value;
        private volatile List<Action<IPropertyChangeEvent<K, V>>> _changeListeners;

        public DefaultProperty(PropertyConfig<K, V> config, V value)
        {
            if (config == null)
                throw new ArgumentNullException("config is null");

            _config = config;
            _value = value;
        }

        IPropertyConfig IProperty.Config { get { return Config; } }

        public virtual PropertyConfig<K, V> Config { get { return _config; } }

        object IProperty.Value { get { return Value; } }

        public virtual V Value { get { return (V)_value; } }

        protected internal virtual void SetValue(object value)
        {
            _value = (V)value;
        }

        public virtual void AddChangeListener(Action<IPropertyChangeEvent<K, V>> changeListener)
        {
            if (changeListener == null)
                throw new ArgumentNullException("changeListener", "changeListener is null");

            lock (this)
            {
                if (_changeListeners == null)
                    _changeListeners = new List<Action<IPropertyChangeEvent<K, V>>>();
                _changeListeners.Add(changeListener);
            }
        }

        protected virtual void RaiseChangeEvent(IPropertyChangeEvent<K, V> @event)
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
            return string.Format("{0} {{ config: {1}, value: {2}, changeListeners: [ {3} ] }}", GetType().Name,
                _config, _value, _changeListeners == null ? null : string.Join(", ", _changeListeners));
        }
    }
}