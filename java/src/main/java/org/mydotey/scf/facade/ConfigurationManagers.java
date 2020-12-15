package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.DefaultConfigurationManager;
import org.mydotey.scf.DefaultConfigurationManagerConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationManagers {

    protected ConfigurationManagers() {

    }

    public static ConfigurationManager newManager() {
        return newManager("application");
    }

    public static ConfigurationManager newManager(String name) {
        ConfigurationManagerConfig config = newConfigBuilder().setName(name).build();
        return newManager(config);
    }

    public static ConfigurationManagerConfig.Builder newConfigBuilder() {
        return new DefaultConfigurationManagerConfig.Builder();
    }

    public static ConfigurationManager newManager(ConfigurationManagerConfig config) {
        return new DefaultConfigurationManager(config);
    }

}
