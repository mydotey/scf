package org.mydotey.scf;

import org.mydotey.scf.PropertyConfig;

import java.util.HashMap;
import java.util.Map;

import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class TestConfigurationSource extends AbstractConfigurationSource {

    protected Map<String, String> _properties;

    public TestConfigurationSource(ConfigurationSourceConfig config, HashMap<String, String> properties) {
        super(config);

        init();

        if (properties != null)
            properties.forEach((k, v) -> {
                if (k != null && v != null)
                    _properties.put(k, v);
            });
    }

    protected void init() {
        _properties = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig) {
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

}
