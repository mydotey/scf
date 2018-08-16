package org.mydotey.scf.source.stringproperty.propertiesfile;

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

    @Override
    public String toString() {
        return String.format("%s { name: %s, fileName: %s }", getClass().getSimpleName(), getName(), getFileName());
    }

    public static class Builder extends
            DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder, PropertiesFileConfigurationSourceConfig> {

        @Override
        protected PropertiesFileConfigurationSourceConfig newConfig() {
            return new PropertiesFileConfigurationSourceConfig();
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
