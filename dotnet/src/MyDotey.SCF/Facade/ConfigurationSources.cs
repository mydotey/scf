using System;

namespace MyDotey.SCF.Facade
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class ConfigurationSources
    {
        protected ConfigurationSources()
        {

        }

        public static ConfigurationSourceConfig newConfig(String name)
        {
            return newConfigBuilder().setName(name).build();
        }

        public static ConfigurationSourceConfig.Builder newConfigBuilder()
        {
            return new DefaultConfigurationSourceConfig.Builder();
        }
    }
}