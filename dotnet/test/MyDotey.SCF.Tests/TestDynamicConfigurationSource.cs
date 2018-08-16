using System;
using System.Collections.Generic;
using System.Collections.Concurrent;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class TestDynamicConfigurationSource : TestConfigurationSource
    {
        public TestDynamicConfigurationSource(ConfigurationSourceConfig config, Dictionary<String, String> properties)

            : base(config, properties)
        {
        }

        protected override void Init()
        {
            _properties = new ConcurrentDictionary<String, String>();
        }

        public virtual void SetPropertyValue(String key, String value)
        {
            _properties.TryGetValue(key, out String oldValue);
            if (Object.Equals(oldValue, value))
                return;

            if (value == null)
                _properties.Remove(key);
            else
                _properties[key] = value;

            RaiseChangeEvent();
        }
    }
}