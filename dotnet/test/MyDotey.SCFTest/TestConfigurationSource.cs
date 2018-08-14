using System;
using System.Collections.Generic;
using System.Linq;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class TestConfigurationSource : AbstractConfigurationSource<ConfigurationSourceConfig>
    {
        protected IDictionary<String, String> _properties;

        public TestConfigurationSource(ConfigurationSourceConfig config, Dictionary<String, String> properties)
            : base(config)
        {
            init();

            if (properties != null)
                properties.ToList().ForEach(p =>
                {
                    if (p.Key != null && p.Value != null)
                        _properties[p.Key] = p.Value;
                });
        }

        protected virtual void init()
        {
            _properties = new Dictionary<String, String>();
        }

        protected override Object getPropertyValue(Object key)
        {
            _properties.TryGetValue((String)key, out String value);
            return value;
        }
    }
}