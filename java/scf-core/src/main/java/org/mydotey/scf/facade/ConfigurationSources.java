package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSource;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.systemproperties.SystemPropertiesConfigurationSource;
import org.mydotey.scf.source.stringproperties.systemproperties.SystemPropertiesConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationSources {

    protected ConfigurationSources() {

    }

    public static PropertiesFileConfigurationSourceConfig.Builder newPropertiesFileSourceConfigBuilder() {
        return new PropertiesFileConfigurationSourceConfig.Builder();
    }

    public static ConfigurationSource newPropertiesFileSource(PropertiesFileConfigurationSourceConfig config) {
        return new PropertiesFileConfigurationSource(config);
    }

    public static SystemPropertiesConfigurationSourceConfig.Builder newSystemPropertiesSourceConfigBuilder() {
        return new SystemPropertiesConfigurationSourceConfig.Builder();
    }

    public static ConfigurationSource newSystemPropertiesSource(SystemPropertiesConfigurationSourceConfig config) {
        return new SystemPropertiesConfigurationSource(config);
    }

}
