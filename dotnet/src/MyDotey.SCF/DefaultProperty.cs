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
        private volatile IConfigurationSource _source;
        private volatile List<EventHandler<IPropertyChangeEvent<K, V>>> _changeListeners;

        public DefaultProperty(PropertyConfig<K, V> config, V value, IConfigurationSource source)
        {
            if (config == null)
                throw new ArgumentNullException("config is null");

            _config = config;
            _value = value;
            _source = source;
        }

        IPropertyConfig IProperty.Config { get { return Config; } }

        public virtual PropertyConfig<K, V> Config { get { return _config; } }

        object IProperty.Value { get { return Value; } }

        public virtual V Value { get { return (V)_value; } }

        public virtual IConfigurationSource Source { get { return _source; } }

        protected internal virtual void Update(object value, IConfigurationSource source)
        {
            _value = (V)value;
            _source = source;
        }

        public virtual event EventHandler<IPropertyChangeEvent<K, V>> OnChange
        {
            add
            {
                if (value == null)
                    throw new ArgumentNullException("changeListener is null");

                lock (this)
                {
                    if (_changeListeners == null)
                        _changeListeners = new List<EventHandler<IPropertyChangeEvent<K, V>>>();
                    _changeListeners.Add(value);
                }
            }

            remove
            {
                throw new NotSupportedException("remove is not supported");
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
                        l(this, @event);
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
            return string.Format("{0} {{ config: {1}, value: {2}, source: {3}, changeListeners: [ {4} ] }}",
                GetType().Name, _config, _value, _source == null ? null : _source.Config.Name,
                _changeListeners == null ? null : string.Join(", ", _changeListeners));
        }
    }
}