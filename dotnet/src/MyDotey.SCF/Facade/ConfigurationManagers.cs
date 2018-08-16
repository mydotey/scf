using System;

namespace MyDotey.SCF.Facade
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class ConfigurationManagers
    {

        protected ConfigurationManagers()
        {

        }

        public static ConfigurationManagerConfig.IBuilder NewConfigBuilder()
        {
            return new DefaultConfigurationManagerConfig.Builder();
        }

        public static IConfigurationManager NewManager(ConfigurationManagerConfig config)
        {
            return new DefaultConfigurationManager(config);
        }
    }
}