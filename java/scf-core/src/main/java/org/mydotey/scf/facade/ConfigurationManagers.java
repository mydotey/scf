package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.DefaultConfigurationManager;
import org.mydotey.scf.DefaultConfigurationManagerConfig;
import org.mydotey.scf.DefaultPropertyConfig;
import org.mydotey.scf.PropertyConfig;

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

    public static ConfigurationManagerConfig.Builder newManagerConfigBuilder() {
        return new DefaultConfigurationManagerConfig.Builder();
    }

    public static ConfigurationManager newManager(ConfigurationManagerConfig config) {
        return new DefaultConfigurationManager(config);
    }

}
