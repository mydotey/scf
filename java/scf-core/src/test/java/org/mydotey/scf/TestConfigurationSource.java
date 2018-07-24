package org.mydotey.scf;

import java.util.HashMap;
import java.util.Map;

import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class TestConfigurationSource extends AbstractConfigurationSource {

    protected Map<String, String> _properties;

    public TestConfigurationSource(ConfigurationSourceConfig config, HashMap<String, String> properties) {
        super(config);

        init();

        if (properties != null)
            properties.forEach((k, v) -> {
                if (k != null && v != null)
                    _properties.put(k, v);
            });
    }

    protected void init() {
        _properties = new HashMap<>();
    }

    @Override
    protected Object getPropertyValue(Object key) {
        return _properties.get(key);
    }

}
