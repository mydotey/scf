using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * Jul 19, 2018
     */
    public class DefaultPropertyChangeEvent<K, V> : IPropertyChangeEvent<K, V>
    {
        private IProperty<K, V> _property;
        private V _oldValue;
        private V _newValue;
        private long _changeTime;

        public DefaultPropertyChangeEvent(IProperty<K, V> property, V oldValue, V newValue)
            : this(property, oldValue, newValue, DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond)
        {

        }

        public DefaultPropertyChangeEvent(IProperty<K, V> property, V oldValue, V newValue, long changeTime)
        {
            if (property == null)
                throw new ArgumentNullException("property is null");

            _property = property;
            _oldValue = oldValue;
            _newValue = newValue;
            _changeTime = changeTime;
        }

        IProperty IPropertyChangeEvent.Property { get { return Property; } }

        public virtual IProperty<K, V> Property { get { return _property; } }

        object IPropertyChangeEvent.OldValue { get { return OldValue; } }

        public virtual V OldValue { get { return _oldValue; } }

        object IPropertyChangeEvent.NewValue { get { return NewValue; } }

        public virtual V NewValue { get { return _newValue; } }

        public virtual long ChangeTime { get { return _changeTime; } }

        public override String ToString()
        {
            return string.Format("{0} {{ property: {1}, oldValue: {2}, newValue: {3}, changeTime: {4} }}",
                    GetType().Name, _property, _oldValue, _newValue, _changeTime);
        }
    }
}