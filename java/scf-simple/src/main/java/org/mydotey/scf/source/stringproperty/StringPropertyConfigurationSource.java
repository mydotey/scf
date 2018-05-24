package org.mydotey.scf.source.stringproperty;

import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public abstract class StringPropertyConfigurationSource extends AbstractConfigurationSource {

    public StringPropertyConfigurationSource(ConfigurationSourceConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig) {
        if (propertyConfig.getKey().getClass() != String.class)
            return null;

        String value = getPropertyValue((String) propertyConfig.getKey());
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

    public abstract String getPropertyValue(String key);

}
