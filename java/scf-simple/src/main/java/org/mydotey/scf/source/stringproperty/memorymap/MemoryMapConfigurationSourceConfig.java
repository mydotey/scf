package org.mydotey.scf.source.stringproperty.memorymap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mydotey.scf.DefaultConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class MemoryMapConfigurationSourceConfig extends DefaultConfigurationSourceConfig {

    private Map<String, String> _properties;

    protected MemoryMapConfigurationSourceConfig() {

    }

    public Map<String, String> getProperties() {
        return _properties;
    }

    @Override
    public MemoryMapConfigurationSourceConfig clone() {
        MemoryMapConfigurationSourceConfig copy = (MemoryMapConfigurationSourceConfig) super.clone();

        if (_properties != null)
            copy._properties = Collections.unmodifiableMap(new HashMap<>(_properties));

        return copy;
    }

    @Override
    public String toString() {
        return String.format("%s { name: %s, priority: %d, properties: %s }", getClass().getSimpleName(), getName(),
                getPriority(), getProperties());
    }

    public static class Builder extends DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder> {

        @Override
        protected DefaultConfigurationSourceConfig newConfig() {
            return new MemoryMapConfigurationSourceConfig();
        }

        @Override
        protected MemoryMapConfigurationSourceConfig getConfig() {
            return (MemoryMapConfigurationSourceConfig) super.getConfig();
        }

        public Builder addProperty(String key, String value) {
            if (key != null && value != null) {
                if (getConfig()._properties == null)
                    getConfig()._properties = new HashMap<>();
                getConfig()._properties.put(key, value);
            }

            return this;
        }

        public Builder addProperties(Map<String, String> properties) {
            if (properties != null)
                properties.forEach(this::addProperty);

            return this;
        }

        @Override
        public MemoryMapConfigurationSourceConfig build() {
            return (MemoryMapConfigurationSourceConfig) super.build();
        }

    }

}
