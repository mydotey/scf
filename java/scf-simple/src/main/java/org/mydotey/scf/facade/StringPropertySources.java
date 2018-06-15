package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.StringPropertyConfigurationSource;
import org.mydotey.scf.source.stringproperty.cascaded.CascadedConfigurationSource;
import org.mydotey.scf.source.stringproperty.cascaded.CascadedConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.environmentvariable.EnvironmentVariableConfigurationSource;
import org.mydotey.scf.source.stringproperty.memorymap.MemoryMapConfigurationSource;
import org.mydotey.scf.source.stringproperty.memorymap.MemoryMapConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.propertiesfile.PropertiesFileConfigurationSource;
import org.mydotey.scf.source.stringproperty.propertiesfile.PropertiesFileConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.systemproperties.SystemPropertiesConfigurationSource;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public class StringPropertySources {

    protected StringPropertySources() {

    }

    public static SystemPropertiesConfigurationSource newSystemPropertiesSource(ConfigurationSourceConfig config) {
        return new SystemPropertiesConfigurationSource(config);
    }

    public static EnvironmentVariableConfigurationSource newEnvironmentVariableSource(
            ConfigurationSourceConfig config) {
        return new EnvironmentVariableConfigurationSource(config);
    }

    public static MemoryMapConfigurationSourceConfig.Builder newMemoryMapSourceConfigBuilder() {
        return new MemoryMapConfigurationSourceConfig.Builder();
    }

    public static MemoryMapConfigurationSource newMemoryMapSource(MemoryMapConfigurationSourceConfig config) {
        return new MemoryMapConfigurationSource(config);
    }

    public static PropertiesFileConfigurationSourceConfig.Builder newPropertiesFileSourceConfigBuilder() {
        return new PropertiesFileConfigurationSourceConfig.Builder();
    }

    public static PropertiesFileConfigurationSource newPropertiesFileSource(
            PropertiesFileConfigurationSourceConfig config) {
        return new PropertiesFileConfigurationSource(config);
    }

    public static CascadedConfigurationSourceConfig.Builder newCascadedSourceConfigBuilder() {
        return new CascadedConfigurationSourceConfig.Builder();
    }

    public static CascadedConfigurationSource newCascadedSource(CascadedConfigurationSourceConfig config,
            StringPropertyConfigurationSource source) {
        return new CascadedConfigurationSource(config, source);
    }

}
