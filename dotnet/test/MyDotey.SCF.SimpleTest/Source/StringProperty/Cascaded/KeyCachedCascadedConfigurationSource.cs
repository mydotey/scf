using System;

using MyDotey.Collections.Generic;
using MyDotey.SCF.Facade;

namespace MyDotey.SCF.Source.StringProperty.Cascaded
{
    /**
     * @author koqizhao
     *
     * Jul 20, 2018
     * 
     * use CascadedKeyDictionary to cache the keys so as to prevent temp string creation and have better young gc
     */
    public class KeyCachedCascadedConfigurationSource<C> : CascadedConfigurationSource<C>
        where C : ConfigurationSourceConfig
    {
        private CascadedKeyDictionary<String, String> _cascadedKeyDictionary;

        public KeyCachedCascadedConfigurationSource(CascadedConfigurationSourceConfig<C> config)
            : base(config)
        {
            _cascadedKeyDictionary = new CascadedKeyDictionary<String, String>();
        }

        protected override String getKey(params String[] keyParts)
        {
            return _cascadedKeyDictionary.GetOrAdd(base.getKey, keyParts);
        }
    }
}