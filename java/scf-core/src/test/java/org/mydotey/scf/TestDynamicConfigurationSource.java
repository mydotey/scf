package org.mydotey.scf;

import org.mydotey.scf.PropertyConfig;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class TestDynamicConfigurationSource extends AbstractConfigurationSource {

    private ConcurrentHashMap<String, String> _properties;

    public TestDynamicConfigurationSource(ConfigurationSourceConfig config, HashMap<String, String> properties) {
        super(config);
        _properties = new ConcurrentHashMap<>(properties);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <K, V> V doGetPropertyValue(PropertyConfig<K, V> propertyConfig) {
        if (propertyConfig.getKey().getClass() != String.class)
            return null;

        String value = _properties.get(propertyConfig.getKey());
        if (value == null || value.isEmpty())
            return null;

        value = value.trim();
        if (value.isEmpty())
            return null;

        if (propertyConfig.getValueType() == String.class)
            return (V) value;

        for (TypeConverter<?, ?> typeConverter : propertyConfig.getValueConverters()) {
            if (typeConverter.getSourceType() == String.class
                    && propertyConfig.getValueType().isAssignableFrom(typeConverter.getTargetType()))
                return ((TypeConverter<String, V>) typeConverter).convert(value);
        }

        return null;
    }

    public void setPropertyValue(String key, String value) {
        String oldValue = _properties.get(key);
        if (Objects.equals(oldValue, value))
            return;

        if (value == null)
            _properties.remove(key);
        else
            _properties.put(key, value);

        raiseChangeEvent();
    }

}
