package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.impl.DefaultConfigurationManager;
import org.mydotey.scf.impl.DefaultConfigurationManagerConfig;
import org.mydotey.scf.impl.DefaultPropertyConfig;

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
