package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.DefaultConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationSources {

    protected ConfigurationSources() {

    }

    public static ConfigurationSourceConfig newConfig(String name) {
        return newConfigBuilder().setName(name).build();
    }

    public static ConfigurationSourceConfig.Builder newConfigBuilder() {
        return new DefaultConfigurationSourceConfig.Builder();
    }

}
