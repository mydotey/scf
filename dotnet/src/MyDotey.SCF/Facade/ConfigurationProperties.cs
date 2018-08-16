using System;

namespace MyDotey.SCF.Facade
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class ConfigurationProperties
    {
        protected ConfigurationProperties()
        {

        }

        public static PropertyConfig<K, V>.IBuilder NewConfigBuilder<K, V>()
        {
            return new DefaultPropertyConfig<K, V>.Builder();
        }
    }
}