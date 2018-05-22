package org.mydotey.scf.facade;

import org.mydotey.scf.source.cascaded.CascadedConfigurationSource;
import org.mydotey.scf.source.cascaded.CascadedConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.StringPropertiesConfigurationSource;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public class MoreSources extends ConfigurationSources {

    protected MoreSources() {

    }

    public static CascadedConfigurationSourceConfig.Builder newCascadedSourceConfigBuilder() {
        return new CascadedConfigurationSourceConfig.Builder();
    }

    public static CascadedConfigurationSource newCascadedSource(CascadedConfigurationSourceConfig config,
            StringPropertiesConfigurationSource source) {
        return new CascadedConfigurationSource(config, source);
    }

}
