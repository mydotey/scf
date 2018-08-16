using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 16, 2018
     */
    public interface ConfigurationSource
    {
        /**
         * @see ConfigurationSourceConfig
         */
        ConfigurationSourceConfig getConfig();

        /**
         * get property value acccording to the property config
         * <p>
         * if property is configured, the value is of type V
         *      or can be converted to type V by the converters @see {@link PropertyConfig#getValueConverters()},
         *      a value of type V returned
         * <p>
         * otherwise, null returned
         */
        V getPropertyValue<K, V>(PropertyConfig<K, V> propertyConfig);

        /**
         * listeners to the source change, notified once source changed
         * <p>
         * will be used by the configuration manager
         */
        void addChangeListener(Action<ConfigurationSourceChangeEvent> changeListener);
    }
}