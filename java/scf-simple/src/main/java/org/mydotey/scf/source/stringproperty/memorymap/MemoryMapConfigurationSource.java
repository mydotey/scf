package org.mydotey.scf.source.stringproperty.memorymap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.mydotey.scf.source.stringproperty.StringPropertyConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class MemoryMapConfigurationSource extends StringPropertyConfigurationSource {

    private ConcurrentHashMap<String, String> _properties;

    public MemoryMapConfigurationSource(MemoryMapConfigurationSourceConfig config) {
        super(config);

        _properties = new ConcurrentHashMap<>();
        if (config.getProperties() != null)
            _properties.putAll(config.getProperties());
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
