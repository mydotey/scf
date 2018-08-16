using System;

namespace MyDotey.SCF.Source
{
    /**
     * @author koqizhao
     *
     * Jul 16, 2018
     * 
     * Nothing Done Configuration Source
     */
    public class NullConfigurationSource : ConfigurationSource
    {
        public static readonly NullConfigurationSource INSTANCE = new NullConfigurationSource();

        private ConfigurationSourceConfig _config = NullConfigurationSourceConfig.INSTANCE;

        private NullConfigurationSource()
        {

        }

        /**
         * always null
         */
        public V getPropertyValue<K, V>(PropertyConfig<K, V> propertyConfig)
        {
            return default(V);
        }

        /**
         * config with name "null-configuration-source"
         */
        public ConfigurationSourceConfig getConfig()
        {
            return _config;
        }

        /**
         * always ignore the listeners
         */
        public void addChangeListener(Action<ConfigurationSourceChangeEvent> changeListener)
        {

        }

        public override string ToString()
        {
            return _config.getName();
        }

        private class NullConfigurationSourceConfig : ConfigurationSourceConfig
        {
            public static readonly NullConfigurationSourceConfig INSTANCE = new NullConfigurationSourceConfig();

            private NullConfigurationSourceConfig()
            {

            }

            public override string getName()
            {
                return "null-configuration-source";
            }
        }
    }
}