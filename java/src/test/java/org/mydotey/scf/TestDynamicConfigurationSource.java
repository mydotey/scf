package org.mydotey.scf;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.mydotey.scf.ConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class TestDynamicConfigurationSource extends TestConfigurationSource {

    public TestDynamicConfigurationSource(ConfigurationSourceConfig config, HashMap<String, String> properties) {
        super(config, properties);
    }

    @Override
    protected void init() {
        _properties = new ConcurrentHashMap<>();
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

}
