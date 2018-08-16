using System;

namespace MyDotey.SCF
{
    public interface IProperty
    {
        IPropertyConfig Config { get; }
        object Value { get; }
    }

    /**
     * @author koqizhao
     *
     * May 16, 2018
     */
    public interface IProperty<K, V> : IProperty
    {
        /**
         * @see PropertyConfig
         */
        new PropertyConfig<K, V> Config { get; }

        /**
         * property value, if not configured or no valid value, default to defaultValue Of PropertyConfig
         * <p>
         * @see PropertyConfig#getDefaultValue()
         */
        new V Value { get; }

        /**
         * listeners to the value change, notified once value changed
         */
        void AddChangeListener(Action<IPropertyChangeEvent<K, V>> changeListener);
    }
}