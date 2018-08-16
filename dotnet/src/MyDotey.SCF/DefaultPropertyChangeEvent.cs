using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * Jul 19, 2018
     */
    public class DefaultPropertyChangeEvent<K, V> : PropertyChangeEvent<K, V>
    {
        private Property<K, V> _property;
        private V _oldValue;
        private V _newValue;
        private long _changeTime;

        public DefaultPropertyChangeEvent(Property<K, V> property, V oldValue, V newValue)
            : this(property, oldValue, newValue, DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond)
        {

        }

        public DefaultPropertyChangeEvent(Property<K, V> property, V oldValue, V newValue, long changeTime)
        {
            if (property == null)
                throw new ArgumentNullException("property is null");

            _property = property;
            _oldValue = oldValue;
            _newValue = newValue;
            _changeTime = changeTime;
        }

        Property PropertyChangeEvent.getProperty()
        {
            return getProperty();
        }

        public virtual Property<K, V> getProperty()
        {
            return _property;
        }

        object PropertyChangeEvent.getOldValue()
        {
            return getOldValue();
        }

        public virtual V getOldValue()
        {
            return _oldValue;
        }

        object PropertyChangeEvent.getNewValue()
        {
            return getNewValue();
        }

        public virtual V getNewValue()
        {
            return _newValue;
        }

        public virtual long getChangeTime()
        {
            return _changeTime;
        }

        public override String ToString()
        {
            return string.Format("{0} {{ property: {1}, oldValue: {2}, newValue: {3}, changeTime: {4} }}",
                    GetType().Name, _property, _oldValue, _newValue, _changeTime);
        }
    }
}