package org.mydotey.scf.source.stringproperties.propertiesfile;

import org.mydotey.scf.DefaultConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class PropertiesFileConfigurationSourceConfig extends DefaultConfigurationSourceConfig {

    private String _fileName;

    protected PropertiesFileConfigurationSourceConfig() {

    }

    public String getFileName() {
        return _fileName;
    }

    public static class Builder extends DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder> {

        @Override
        protected DefaultConfigurationSourceConfig newConfig() {
            return new PropertiesFileConfigurationSourceConfig();
        }

        @Override
        protected PropertiesFileConfigurationSourceConfig getConfig() {
            return (PropertiesFileConfigurationSourceConfig) super.getConfig();
        }

        public Builder setFileName(String fileName) {
            getConfig()._fileName = fileName;
            return this;
        }

        @Override
        public PropertiesFileConfigurationSourceConfig build() {
            if (getConfig().getFileName() == null || getConfig().getFileName().trim().isEmpty())
                throw new IllegalArgumentException("fileName is null or empty");

            getConfig()._fileName = getConfig()._fileName.trim();
            if (!getConfig()._fileName.endsWith(".properties"))
                getConfig()._fileName += ".properties";

            return (PropertiesFileConfigurationSourceConfig) super.build();
        }

    }

}
