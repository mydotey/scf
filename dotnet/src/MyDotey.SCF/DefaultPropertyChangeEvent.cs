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
        public virtual IProperty<K, V> Property { get; private set; }
        public virtual V OldValue { get; private set; }
        public virtual DateTime ChangeTime { get; private set; }
        public virtual V NewValue { get; private set; }

        public DefaultPropertyChangeEvent(IProperty<K, V> property, V oldValue, V newValue)
            : this(property, oldValue, newValue, DateTime.Now)
        {

        }

        public DefaultPropertyChangeEvent(IProperty<K, V> property, V oldValue, V newValue, DateTime changeTime)
        {
            if (property == null)
                throw new ArgumentNullException("property is null");

            Property = property;
            OldValue = oldValue;
            NewValue = newValue;
            ChangeTime = changeTime;
        }

        IProperty IPropertyChangeEvent.Property { get { return Property; } }

        object IPropertyChangeEvent.OldValue { get { return OldValue; } }

        object IPropertyChangeEvent.NewValue { get { return NewValue; } }

        public override String ToString()
        {
            return string.Format("{0} {{ property: {1}, oldValue: {2}, newValue: {3}, changeTime: {4} }}",
                    GetType().Name, Property, OldValue, NewValue, ChangeTime);
        }
    }
}