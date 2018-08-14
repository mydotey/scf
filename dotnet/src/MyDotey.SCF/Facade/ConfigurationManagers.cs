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

        public static ConfigurationManagerConfig.Builder newConfigBuilder()
        {
            return new DefaultConfigurationManagerConfig.Builder();
        }

        public static ConfigurationManager newManager(ConfigurationManagerConfig config)
        {
            return new DefaultConfigurationManager(config);
        }
    }
}