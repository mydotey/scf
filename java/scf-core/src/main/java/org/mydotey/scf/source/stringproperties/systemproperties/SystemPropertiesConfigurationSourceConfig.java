package org.mydotey.scf.source.stringproperties.systemproperties;

import org.mydotey.scf.AbstractConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class SystemPropertiesConfigurationSourceConfig extends AbstractConfigurationSourceConfig {

    protected SystemPropertiesConfigurationSourceConfig() {

    }

    public static class Builder extends AbstractConfigurationSourceConfig.AbstractBuilder<Builder> {

        @Override
        protected AbstractConfigurationSourceConfig newConfig() {
            return new SystemPropertiesConfigurationSourceConfig();
        }

        @Override
        public SystemPropertiesConfigurationSourceConfig build() {
            return (SystemPropertiesConfigurationSourceConfig) super.build();
        }

    }

}
