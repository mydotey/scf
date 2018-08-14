using System;
using System.Collections.Generic;
using System.Text;

namespace MyDotey.SCF.Source.StringProperty.Cascaded
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     * 
     * allow casaded config like:
     *  key
     *  key.a
     *  key.a.b
     * priority:
     *  key.a.b &gt; k.a &gt; key
     */
    public class CascadedConfigurationSource<C> : StringPropertyConfigurationSource<CascadedConfigurationSourceConfig<C>>
        where C : ConfigurationSourceConfig
    {
        private StringPropertyConfigurationSource<C> _source;
        private List<String> _cascadedKeyParts;

        public CascadedConfigurationSource(CascadedConfigurationSourceConfig<C> config)
            : base(config)
        {
            _source = config.getSource();
            _source.addChangeListener(s => raiseChangeEvent());

            init();
        }

        protected virtual void init()
        {
            _cascadedKeyParts = new List<String>();

            StringBuilder keyPart = new StringBuilder("");
            _cascadedKeyParts.Add(keyPart.ToString());
            foreach (String factor in getConfig().getCascadedFactors())
            {
                keyPart.Append(getConfig().getKeySeparator()).Append(factor);
                _cascadedKeyParts.Add(keyPart.ToString());
            }

            _cascadedKeyParts.Reverse();
        }

        public override String getPropertyValue(String key)
        {
            foreach (String keyPart in _cascadedKeyParts)
            {
                String cascadedKey = getKey(key, keyPart);
                String value = _source.getPropertyValue(cascadedKey);
                if (value != null)
                    return value;
            }

            return null;
        }

        /**
         * allow user to override
         * if the key count is limited, can cache the key and have less memory use
         * @param keyParts
         * @return property value
         */
        protected virtual String getKey(params String[] keyParts)
        {
            if (keyParts == null)
                return null;

            StringBuilder stringBuilder = new StringBuilder();
            foreach (String part in keyParts)
                stringBuilder.Append(part);
            return stringBuilder.ToString();
        }
    }
}