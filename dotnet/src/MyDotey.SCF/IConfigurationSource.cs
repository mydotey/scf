using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 16, 2018
     */
    public interface IConfigurationSource
    {
        /**
         * @see ConfigurationSourceConfig
         */
        ConfigurationSourceConfig Config { get; }

        /**
         * get property value acccording to the property config
         * <p>
         * if property is configured, the value is of type V
         *      or can be converted to type V by the converters @see {@link PropertyConfig#getValueConverters()},
         *      a value of type V returned
         * <p>
         * otherwise, null returned
         */
        V GetPropertyValue<K, V>(PropertyConfig<K, V> propertyConfig);

        /**
         * listeners to the source change, notified once source changed
         * <p>
         * will be used by the configuration manager
         */
        void AddChangeListener(Action<IConfigurationSourceChangeEvent> changeListener);
    }
}