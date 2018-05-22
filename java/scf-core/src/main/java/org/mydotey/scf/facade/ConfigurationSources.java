package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.DefaultConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.environmentvariable.EnvironmentVariableConfigurationSource;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSource;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.systemproperties.SystemPropertiesConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationSources {

    protected ConfigurationSources() {

    }

    public static ConfigurationSourceConfig.Builder newConfigBuilder() {
        return new DefaultConfigurationSourceConfig.Builder();
    }

    public static SystemPropertiesConfigurationSource newSystemPropertiesSource(ConfigurationSourceConfig config) {
        return new SystemPropertiesConfigurationSource(config);
    }

    public static EnvironmentVariableConfigurationSource newEnvironmentVariableSource(
            ConfigurationSourceConfig config) {
        return new EnvironmentVariableConfigurationSource(config);
    }

    public static PropertiesFileConfigurationSourceConfig.Builder newPropertiesFileSourceConfigBuilder() {
        return new PropertiesFileConfigurationSourceConfig.Builder();
    }

    public static PropertiesFileConfigurationSource newPropertiesFileSource(
            PropertiesFileConfigurationSourceConfig config) {
        return new PropertiesFileConfigurationSource(config);
    }

}
