package org.mydotey.scf.facade;

import org.mydotey.scf.DefaultPropertyConfig;
import org.mydotey.scf.PropertyConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationProperties {

    protected ConfigurationProperties() {

    }

    public static <K, V> PropertyConfig.Builder<K, V> newConfigBuilder() {
        return new DefaultPropertyConfig.Builder<>();
    }

}
