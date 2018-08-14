using System;

namespace MyDotey.SCF.Source.StringProperty.EnvironmentVariable
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     * 
     * Use System.getenv to get environment variables
     * 
     * non-dynamic source
     */
    public class EnvironmentVariableConfigurationSource : StringPropertyConfigurationSource<ConfigurationSourceConfig>
    {
        public EnvironmentVariableConfigurationSource(ConfigurationSourceConfig config)
            : base(config)
        {
        }

        public override string getPropertyValue(string key)
        {
            return Environment.GetEnvironmentVariable(key);
        }
    }
}