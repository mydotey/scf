package org.mydotey.scf.source.stringproperties.environmentvariable;

import org.mydotey.scf.AbstractConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class EnvironmentVariableConfigurationSourceConfig extends AbstractConfigurationSourceConfig {

    protected EnvironmentVariableConfigurationSourceConfig() {

    }

    public static class Builder extends AbstractConfigurationSourceConfig.AbstractBuilder<Builder> {

        @Override
        protected AbstractConfigurationSourceConfig newConfig() {
            return new EnvironmentVariableConfigurationSourceConfig();
        }

        @Override
        public EnvironmentVariableConfigurationSourceConfig build() {
            return (EnvironmentVariableConfigurationSourceConfig) super.build();
        }

    }

}
