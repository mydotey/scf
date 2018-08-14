using System;

using MyDotey.SCF.Source.StringProperty;
using MyDotey.SCF.Source.StringProperty.Cascaded;
using MyDotey.SCF.Source.StringProperty.EnvironmentVariable;
using MyDotey.SCF.Source.StringProperty.MemoryMap;
using MyDotey.SCF.Source.StringProperty.PropertiesFile;

namespace MyDotey.SCF.Facade
{
    /**
     * @author koqizhao
     *
     * May 22, 2018
     */
    public class StringPropertySources
    {
        protected StringPropertySources()
        {

        }

        public static EnvironmentVariableConfigurationSource newEnvironmentVariableSource(String name)
        {
            ConfigurationSourceConfig config = ConfigurationSources.newConfig(name);
            return new EnvironmentVariableConfigurationSource(config);
        }

        public static MemoryMapConfigurationSource newMemoryMapSource(String name)
        {
            ConfigurationSourceConfig config = ConfigurationSources.newConfig(name);
            return new MemoryMapConfigurationSource(config);
        }

        public static PropertiesFileConfigurationSourceConfig.Builder newPropertiesFileSourceConfigBuilder()
        {
            return new PropertiesFileConfigurationSourceConfig.Builder();
        }

        public static PropertiesFileConfigurationSource newPropertiesFileSource(
                PropertiesFileConfigurationSourceConfig config)
        {
            return new PropertiesFileConfigurationSource(config);
        }

        public static CascadedConfigurationSourceConfig<C>.Builder newCascadedSourceConfigBuilder<C>()
            where C : ConfigurationSourceConfig
        {
            return new CascadedConfigurationSourceConfig<C>.Builder();
        }

        public static CascadedConfigurationSource<C> newCascadedSource<C>(CascadedConfigurationSourceConfig<C> config)
            where C : ConfigurationSourceConfig
        {
            return new CascadedConfigurationSource<C>(config);
        }
    }
}