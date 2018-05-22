package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.DefaultConfigurationManager;
import org.mydotey.scf.DefaultConfigurationManagerConfig;
import org.mydotey.scf.DefaultPropertyConfig;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSource;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.systemproperties.SystemPropertiesConfigurationSource;
import org.mydotey.scf.source.stringproperties.systemproperties.SystemPropertiesConfigurationSourceConfig;

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

    public static SystemPropertiesConfigurationSourceConfig.Builder newSystemPropertiesSourceConfigBuilder() {
        return new SystemPropertiesConfigurationSourceConfig.Builder();
    }

    public static ConfigurationSource newSystemPropertiesSource(SystemPropertiesConfigurationSourceConfig config) {
        return new SystemPropertiesConfigurationSource(config);
    }

    public static ConfigurationManagerConfig.Builder newManagerConfigBuilder() {
        return new DefaultConfigurationManagerConfig.Builder();
    }

    public static ConfigurationManager newManager(ConfigurationManagerConfig config) {
        return new DefaultConfigurationManager(config);
    }

}
