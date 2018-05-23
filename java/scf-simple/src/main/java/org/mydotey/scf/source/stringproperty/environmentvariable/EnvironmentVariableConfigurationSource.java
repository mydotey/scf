package org.mydotey.scf.source.stringproperty.environmentvariable;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.StringPropertyConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class EnvironmentVariableConfigurationSource extends StringPropertyConfigurationSource {

    public EnvironmentVariableConfigurationSource(ConfigurationSourceConfig config) {
        super(config);
    }

    @Override
    public String getPropertyValue(String key) {
        return System.getenv(key);
    }

}
