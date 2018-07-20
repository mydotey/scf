package org.mydotey.scf.source.stringproperty.cascaded;

import org.mydotey.collection.CascadedKeyMap;
import org.mydotey.scf.source.stringproperty.StringPropertyConfigurationSource;

/**
 * @author koqizhao
 *
 * Jul 20, 2018
 * 
 * use CascadedKeyMap to cache the keys so as to prevent temp string creation and have better young gc
 */
public class KeyCachedCascadedConfigurationSource extends CascadedConfigurationSource {

    private CascadedKeyMap<String, String> _cascadedKeyMap;

    public KeyCachedCascadedConfigurationSource(CascadedConfigurationSourceConfig config,
            StringPropertyConfigurationSource source) {
        super(config, source);

        _cascadedKeyMap = new CascadedKeyMap<>();
    }

    @Override
    protected String getKey(String... keyParts) {
        return _cascadedKeyMap.getOrAdd(super::getKey, keyParts);
    }

}
