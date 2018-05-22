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
    public String getPropertyValue(String key) {
        return System.getProperty(key);
    }

    public void setPropertyValue(String key, String value) {
        String oldValue = System.getProperty(key);
        if (Objects.equals(oldValue, value))
            return;

        System.setProperty(key, value);
        raiseChangeEvent();
    }

    public void clearProperty(String key) {
        String oldValue = System.getProperty(key);
        if (oldValue == null)
            return;

        System.clearProperty(key);
        raiseChangeEvent();
    }

    public void setProperties(Properties properties) {
        System.setProperties(properties);

        raiseChangeEvent();
    }

}
