package org.mydotey.scf.source.stringproperties.environmentvariable;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperties.StringPropertiesConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class EnvironmentVariableConfigurationSource extends StringPropertiesConfigurationSource {

    public EnvironmentVariableConfigurationSource(ConfigurationSourceConfig config) {
        super(config);
    }

    @Override
    protected String getPropertyValue(String key) {
        return System.getenv(key);
    }

}
