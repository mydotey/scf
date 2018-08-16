package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.cascaded.CascadedConfigurationSource;
import org.mydotey.scf.source.stringproperty.cascaded.CascadedConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.environmentvariable.EnvironmentVariableConfigurationSource;
import org.mydotey.scf.source.stringproperty.memorymap.MemoryMapConfigurationSource;
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

    public static SystemPropertiesConfigurationSource newSystemPropertiesSource(String name) {
        ConfigurationSourceConfig config = ConfigurationSources.newConfig(name);
        return new SystemPropertiesConfigurationSource(config);
    }

    public static EnvironmentVariableConfigurationSource newEnvironmentVariableSource(String name) {
        ConfigurationSourceConfig config = ConfigurationSources.newConfig(name);
        return new EnvironmentVariableConfigurationSource(config);
    }

    public static MemoryMapConfigurationSource newMemoryMapSource(String name) {
        ConfigurationSourceConfig config = ConfigurationSources.newConfig(name);
        return new MemoryMapConfigurationSource(config);
    }

    public static PropertiesFileConfigurationSourceConfig.Builder newPropertiesFileSourceConfigBuilder() {
        return new PropertiesFileConfigurationSourceConfig.Builder();
    }

    public static PropertiesFileConfigurationSource newPropertiesFileSource(
            PropertiesFileConfigurationSourceConfig config) {
        return new PropertiesFileConfigurationSource(config);
    }

    public static <C extends ConfigurationSourceConfig> CascadedConfigurationSourceConfig.Builder<C> newCascadedSourceConfigBuilder() {
        return new CascadedConfigurationSourceConfig.Builder<>();
    }

    public static <C extends ConfigurationSourceConfig> CascadedConfigurationSource<C> newCascadedSource(
            CascadedConfigurationSourceConfig<C> config) {
        return new CascadedConfigurationSource<>(config);
    }

}
