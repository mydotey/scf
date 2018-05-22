package org.mydotey.scf.source.stringproperties.systemproperties;

import java.util.Objects;
import java.util.Properties;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.StringPropertiesConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class SystemPropertiesConfigurationSource extends StringPropertiesConfigurationSource {

    public SystemPropertiesConfigurationSource(ConfigurationSourceConfig config) {
        super(config);

        setDynamic(true);
    }

    @Override
    protected String getPropertyValue(String key) {
        return System.getProperty(key);
    }

    public void setPropertyValue(String key, String value) {
        String oldValue = System.getProperty(key);
        if (Objects.equals(oldValue, value))
            return;

        System.setProperty(key, value);
        raiseChangeEvent();
    }

    public void setProperties(Properties properties) {
        System.setProperties(properties);

        raiseChangeEvent();
    }

}
