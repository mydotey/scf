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

        public static ConfigurationSourceConfig NewConfig(String name)
        {
            return NewConfigBuilder().SetName(name).Build();
        }

        public static ConfigurationSourceConfig.IBuilder NewConfigBuilder()
        {
            return new DefaultConfigurationSourceConfig.Builder();
        }
    }
}