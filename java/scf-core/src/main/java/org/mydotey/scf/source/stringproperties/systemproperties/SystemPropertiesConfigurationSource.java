package org.mydotey.scf.source.stringproperties.systemproperties;

import org.mydotey.scf.source.stringproperties.StringPropertiesConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class SystemPropertiesConfigurationSource extends StringPropertiesConfigurationSource {

    public SystemPropertiesConfigurationSource(SystemPropertiesConfigurationSourceConfig config) {
        super(config);
    }

    @Override
    protected String getPropertyValue(String key) {
        return System.getProperty(key);
    }

}
