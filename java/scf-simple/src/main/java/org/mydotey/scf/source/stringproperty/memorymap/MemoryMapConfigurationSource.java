package org.mydotey.scf.source.stringproperty.memorymap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.StringPropertyConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 * 
 * all properties stored in a memory concurrent HashMap
 * 
 * dynamic source
 */
public class MemoryMapConfigurationSource extends StringPropertyConfigurationSource<ConfigurationSourceConfig> {

    private ConcurrentHashMap<String, String> _properties;

    public MemoryMapConfigurationSource(ConfigurationSourceConfig config) {
        super(config);

        _properties = new ConcurrentHashMap<>();
    }

    @Override
    public String getPropertyValue(String key) {
        return _properties.get(key);
    }

    public void setPropertyValue(String key, String value) {
        String oldValue = _properties.get(key);
        if (Objects.equals(oldValue, value))
            return;

        if (value == null)
            _properties.remove(key);
        else
            _properties.put(key, value);

        raiseChangeEvent();
    }

    public void setProperties(Map<String, String> properties) {
        _properties.putAll(properties);

        raiseChangeEvent();
    }

}
