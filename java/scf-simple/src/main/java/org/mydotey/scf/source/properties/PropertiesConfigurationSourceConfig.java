package org.mydotey.scf.source.properties;

import org.mydotey.scf.AbstractConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class PropertiesConfigurationSourceConfig extends AbstractConfigurationSourceConfig {

    private String _fileName;

    protected PropertiesConfigurationSourceConfig() {

    }

    public String getFileName() {
        return _fileName;
    }

    public static class Builder extends AbstractConfigurationSourceConfig.AbstractBuilder<Builder> {

        @Override
        protected AbstractConfigurationSourceConfig newConfig() {
            return new PropertiesConfigurationSourceConfig();
        }

        @Override
        protected PropertiesConfigurationSourceConfig getConfig() {
            return (PropertiesConfigurationSourceConfig) super.getConfig();
        }

        public Builder setFileName(String fileName) {
            getConfig()._fileName = fileName;
            return this;
        }

        @Override
        public PropertiesConfigurationSourceConfig build() {
            if (getConfig().getFileName() == null || getConfig().getFileName().trim().isEmpty())
                throw new IllegalArgumentException("fileName is null or empty");

            getConfig()._fileName = getConfig()._fileName.trim();
            if (!getConfig()._fileName.endsWith(".properties"))
                getConfig()._fileName += ".properties";

            return (PropertiesConfigurationSourceConfig) super.build();
        }

    }

}
