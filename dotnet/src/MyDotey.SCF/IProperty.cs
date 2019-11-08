using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public interface IProperty
    {
        IPropertyConfig Config { get; }
        object Value { get; }

        /**
        * which configuration source is actually used
        * return null if using default value
        */
        IConfigurationSource Source { get; }
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
        event EventHandler<IPropertyChangeEvent<K, V>> OnChange;
    }
}