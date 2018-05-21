package org.mydotey.scf.source.properties;

import java.io.InputStream;
import java.util.Properties;

import org.mydotey.scf.impl.AbstractConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class PropertiesConfigurationSource extends AbstractConfigurationSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfigurationSourceConfig.class);

    private Properties _properties;

    public PropertiesConfigurationSource(PropertiesConfigurationSourceConfig config) {
        super(config);

        _properties = new Properties();
        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(config.getFileName())) {
            if (is == null)
                LOGGER.warn("file not found: {}", config.getFileName());

            _properties.load(is);
        } catch (Exception e) {
            LOGGER.warn("failed to load properties file: " + config.getFileName(), e);
        }
    }

    @Override
    protected <K, V> boolean isSupported(K key, Class<V> valueType) {
        return key.getClass() == String.class && valueType == String.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <K, V> V doGetPropertyValue(K key, Class<V> valueType) {
        String value = (String) _properties.get(key);
        if (value == null || value.isEmpty())
            return null;

        value = value.trim();
        if (value.isEmpty())
            return null;

        return (V) value;
    }

}
