package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.DefaultConfigurationManager;
import org.mydotey.scf.DefaultConfigurationManagerConfig;
import org.mydotey.scf.DefaultPropertyConfig;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.source.stringproperties.environmentvariable.EnvironmentVariableConfigurationSource;
import org.mydotey.scf.source.stringproperties.environmentvariable.EnvironmentVariableConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSource;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationManagers {

    protected ConfigurationManagers() {

    }

    public static <K, V> PropertyConfig.Builder<K, V> newPropertyConfigBuilder() {
        return new DefaultPropertyConfig.Builder<>();
    }

    public static PropertiesFileConfigurationSourceConfig.Builder newPropertiesFileSourceConfigBuilder() {
        return new PropertiesFileConfigurationSourceConfig.Builder();
    }

    public static ConfigurationSource newPropertiesFileSource(PropertiesFileConfigurationSourceConfig config) {
        return new PropertiesFileConfigurationSource(config);
    }

    public static EnvironmentVariableConfigurationSourceConfig.Builder newEnvironmentVariableSourceConfigBuilder() {
        return new EnvironmentVariableConfigurationSourceConfig.Builder();
    }

    public static ConfigurationSource newEnvironmentVariableSource(
            EnvironmentVariableConfigurationSourceConfig config) {
        return new EnvironmentVariableConfigurationSource(config);
    }

    public static ConfigurationManagerConfig.Builder newManagerConfigBuilder() {
        return new DefaultConfigurationManagerConfig.Builder();
    }

    public static ConfigurationManager newManager(ConfigurationManagerConfig config) {
        return new DefaultConfigurationManager(config);
    }

}
