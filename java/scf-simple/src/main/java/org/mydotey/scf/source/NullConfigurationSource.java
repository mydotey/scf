package org.mydotey.scf.source;

import java.util.function.Consumer;

import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.ConfigurationSourceChangeEvent;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.PropertyConfig;

/**
 * @author koqizhao
 *
 * Jul 16, 2018
 * 
 * Nothing Done Configuration Source
 */
public class NullConfigurationSource implements ConfigurationSource {

    public static final NullConfigurationSource INSTANCE = new NullConfigurationSource();

    private ConfigurationSourceConfig _config;

    private NullConfigurationSource() {
        _config = new ConfigurationSourceConfig() {
            @Override
            public String getName() {
                return "null-configuration-source";
            }
        };
    }

    /**
     * always null
     */
    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig) {
        return null;
    }

    /**
     * config with name "null-configuration-source"
     */
    @Override
    public ConfigurationSourceConfig getConfig() {
        return _config;
    }

    /**
     * always ignore the listeners
     */
    @Override
    public void addChangeListener(Consumer<ConfigurationSourceChangeEvent> changeListener) {

    }

    @Override
    public String toString() {
        return _config.getName();
    }

}
