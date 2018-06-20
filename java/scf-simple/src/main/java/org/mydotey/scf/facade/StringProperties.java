package org.mydotey.scf.facade;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.Property;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.type.TypeConverter;
import org.mydotey.scf.type.string.StringInplaceConverter;
import org.mydotey.scf.type.string.StringToBooleanConverter;
import org.mydotey.scf.type.string.StringToDoubleConverter;
import org.mydotey.scf.type.string.StringToFloatConverter;
import org.mydotey.scf.type.string.StringToIntConverter;
import org.mydotey.scf.type.string.StringToListConverter;
import org.mydotey.scf.type.string.StringToLongConverter;
import org.mydotey.scf.type.string.StringToMapConverter;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StringProperties {

    private ConfigurationManager _manager;

    public StringProperties(ConfigurationManager manager) {
        Objects.requireNonNull(manager, "manager is null");
        _manager = manager;
    }

    public ConfigurationManager getManager() {
        return _manager;
    }

    public Property<String, String> getStringProperty(String key) {
        return getStringProperty(key, null);
    }

    public String getStringPropertyValue(String key) {
        return getStringPropertyValue(key, null);
    }

    public Property<String, String> getStringProperty(String key, String defaultValue) {
        return getStringProperty(key, defaultValue, null);
    }

    public String getStringPropertyValue(String key, String defaultValue) {
        return getStringPropertyValue(key, defaultValue, null);
    }

    public Property<String, String> getStringProperty(String key, String defaultValue,
            Function<String, String> valueFilter) {
        return getProperty(key, String.class, defaultValue, StringInplaceConverter.DEFAULT, valueFilter);
    }

    public String getStringPropertyValue(String key, String defaultValue, Function<String, String> valueFilter) {
        return getPropertyValue(key, String.class, defaultValue, StringInplaceConverter.DEFAULT, valueFilter);
    }

    public Property<String, Integer> getIntProperty(String key) {
        return getIntProperty(key, null);
    }

    public Integer getIntPropertyValue(String key) {
        return getIntPropertyValue(key, null);
    }

    public Property<String, Integer> getIntProperty(String key, Integer defaultValue) {
        return getIntProperty(key, defaultValue, null);
    }

    public Integer getIntPropertyValue(String key, Integer defaultValue) {
        return getIntPropertyValue(key, defaultValue, null);
    }

    public Property<String, Integer> getIntProperty(String key, Integer defaultValue,
            Function<Integer, Integer> valueFilter) {
        return getProperty(key, Integer.class, defaultValue, StringToIntConverter.DEFAULT, valueFilter);
    }

    public Integer getIntPropertyValue(String key, Integer defaultValue, Function<Integer, Integer> valueFilter) {
        return getPropertyValue(key, Integer.class, defaultValue, StringToIntConverter.DEFAULT, valueFilter);
    }

    public Property<String, Long> getLongProperty(String key) {
        return getLongProperty(key, null);
    }

    public Long getLongPropertyValue(String key) {
        return getLongPropertyValue(key, null);
    }

    public Property<String, Long> getLongProperty(String key, Long defaultValue) {
        return getLongProperty(key, defaultValue, null);
    }

    public Long getLongPropertyValue(String key, Long defaultValue) {
        return getLongPropertyValue(key, defaultValue, null);
    }

    public Property<String, Long> getLongProperty(String key, Long defaultValue, Function<Long, Long> valueFilter) {
        return getProperty(key, Long.class, defaultValue, StringToLongConverter.DEFAULT, valueFilter);
    }

    public Long getLongPropertyValue(String key, Long defaultValue, Function<Long, Long> valueFilter) {
        return getPropertyValue(key, Long.class, defaultValue, StringToLongConverter.DEFAULT, valueFilter);
    }

    public Property<String, Float> getFloatProperty(String key) {
        return getFloatProperty(key, null);
    }

    public Float getFloatPropertyValue(String key) {
        return getFloatPropertyValue(key, null);
    }

    public Property<String, Float> getFloatProperty(String key, Float defaultValue) {
        return getFloatProperty(key, defaultValue, null);
    }

    public Float getFloatPropertyValue(String key, Float defaultValue) {
        return getFloatPropertyValue(key, defaultValue, null);
    }

    public Property<String, Float> getFloatProperty(String key, Float defaultValue,
            Function<Float, Float> valueFilter) {
        return getProperty(key, Float.class, defaultValue, StringToFloatConverter.DEFAULT, valueFilter);
    }

    public Float getFloatPropertyValue(String key, Float defaultValue, Function<Float, Float> valueFilter) {
        return getPropertyValue(key, Float.class, defaultValue, StringToFloatConverter.DEFAULT, valueFilter);
    }

    public Property<String, Double> getDoubleProperty(String key) {
        return getDoubleProperty(key, null);
    }

    public Double getDoublePropertyValue(String key) {
        return getDoublePropertyValue(key, null);
    }

    public Property<String, Double> getDoubleProperty(String key, Double defaultValue) {
        return getDoubleProperty(key, defaultValue, null);
    }

    public Double getDoublePropertyValue(String key, Double defaultValue) {
        return getDoublePropertyValue(key, defaultValue, null);
    }

    public Property<String, Double> getDoubleProperty(String key, Double defaultValue,
            Function<Double, Double> valueFilter) {
        return getProperty(key, Double.class, defaultValue, StringToDoubleConverter.DEFAULT, valueFilter);
    }

    public Double getDoublePropertyValue(String key, Double defaultValue, Function<Double, Double> valueFilter) {
        return getPropertyValue(key, Double.class, defaultValue, StringToDoubleConverter.DEFAULT, valueFilter);
    }

    public Property<String, Boolean> getBooleanProperty(String key) {
        return getBooleanProperty(key, null);
    }

    public Boolean getBooleanPropertyValue(String key) {
        return getBooleanPropertyValue(key, null);
    }

    public Property<String, Boolean> getBooleanProperty(String key, Boolean defaultValue) {
        return getBooleanProperty(key, defaultValue, null);
    }

    public Boolean getBooleanPropertyValue(String key, Boolean defaultValue) {
        return getBooleanPropertyValue(key, defaultValue, null);
    }

    public Property<String, Boolean> getBooleanProperty(String key, Boolean defaultValue,
            Function<Boolean, Boolean> valueFilter) {
        return getProperty(key, Boolean.class, defaultValue, StringToBooleanConverter.DEFAULT, valueFilter);
    }

    public Boolean getBooleanPropertyValue(String key, Boolean defaultValue, Function<Boolean, Boolean> valueFilter) {
        return getPropertyValue(key, Boolean.class, defaultValue, StringToBooleanConverter.DEFAULT, valueFilter);
    }

    public Property<String, List<String>> getListProperty(String key) {
        return getListProperty(key, (List<String>) null);
    }

    public List<String> getListPropertyValue(String key) {
        return getListPropertyValue(key, (List<String>) null);
    }

    public Property<String, List<String>> getListProperty(String key, List<String> defaultValue) {
        return getListProperty(key, defaultValue, StringInplaceConverter.DEFAULT);
    }

    public List<String> getListPropertyValue(String key, List<String> defaultValue) {
        return getListPropertyValue(key, defaultValue, StringInplaceConverter.DEFAULT);
    }

    public <V> Property<String, List<V>> getListProperty(String key, TypeConverter<String, V> typeConverter) {
        return getListProperty(key, null, typeConverter);
    }

    public <V> List<V> getListPropertyValue(String key, TypeConverter<String, V> typeConverter) {
        return getListPropertyValue(key, null, typeConverter);
    }

    public <V> Property<String, List<V>> getListProperty(String key, List<V> defaultValue,
            TypeConverter<String, V> typeConverter) {
        return getListProperty(key, defaultValue, typeConverter, null);
    }

    public <V> List<V> getListPropertyValue(String key, List<V> defaultValue, TypeConverter<String, V> typeConverter) {
        return getListPropertyValue(key, defaultValue, typeConverter, null);
    }

    public <V> Property<String, List<V>> getListProperty(String key, List<V> defaultValue,
            TypeConverter<String, V> typeConverter, Function<List<V>, List<V>> valueFilter) {
        return getProperty(key, (Class) List.class, defaultValue, new StringToListConverter(typeConverter),
                valueFilter);
    }

    public <V> List<V> getListPropertyValue(String key, List<V> defaultValue, TypeConverter<String, V> typeConverter,
            Function<List<V>, List<V>> valueFilter) {
        return getPropertyValue(key, (Class) List.class, defaultValue, new StringToListConverter(typeConverter),
                valueFilter);
    }

    public Property<String, Map<String, String>> getMapProperty(String key) {
        return getMapProperty(key, null);
    }

    public Map<String, String> getMapPropertyValue(String key) {
        return getMapPropertyValue(key, null);
    }

    public Property<String, Map<String, String>> getMapProperty(String key, Map<String, String> defaultValue) {
        return getMapProperty(key, defaultValue, StringInplaceConverter.DEFAULT, StringInplaceConverter.DEFAULT);
    }

    public Map<String, String> getMapPropertyValue(String key, Map<String, String> defaultValue) {
        return getMapPropertyValue(key, defaultValue, StringInplaceConverter.DEFAULT, StringInplaceConverter.DEFAULT);
    }

    public <K, V> Property<String, Map<K, V>> getMapProperty(String key, TypeConverter<String, K> keyConverter,
            TypeConverter<String, V> valueConverter) {
        return getMapProperty(key, null, keyConverter, valueConverter);
    }

    public <K, V> Map<K, V> getMapPropertyValue(String key, TypeConverter<String, K> keyConverter,
            TypeConverter<String, V> valueConverter) {
        return getMapPropertyValue(key, null, keyConverter, valueConverter);
    }

    public <K, V> Property<String, Map<K, V>> getMapProperty(String key, Map<K, V> defaultValue,
            TypeConverter<String, K> keyConverter, TypeConverter<String, V> valueConverter) {
        return getMapProperty(key, defaultValue, keyConverter, valueConverter, null);
    }

    public <K, V> Map<K, V> getMapPropertyValue(String key, Map<K, V> defaultValue,
            TypeConverter<String, K> keyConverter, TypeConverter<String, V> valueConverter) {
        return getMapPropertyValue(key, defaultValue, keyConverter, valueConverter, null);
    }

    public <K, V> Property<String, Map<K, V>> getMapProperty(String key, Map<K, V> defaultValue,
            TypeConverter<String, K> keyConverter, TypeConverter<String, V> valueConverter,
            Function<Map<K, V>, Map<K, V>> valueFilter) {
        return getProperty(key, (Class) Map.class, defaultValue, new StringToMapConverter(keyConverter, valueConverter),
                valueFilter);
    }

    public <K, V> Map<K, V> getMapPropertyValue(String key, Map<K, V> defaultValue,
            TypeConverter<String, K> keyConverter, TypeConverter<String, V> valueConverter,
            Function<Map<K, V>, Map<K, V>> valueFilter) {
        return getPropertyValue(key, (Class) Map.class, defaultValue,
                new StringToMapConverter(keyConverter, valueConverter), valueFilter);
    }

    public <V> Property<String, V> getProperty(String key, Class<V> valueType, V defaultValue,
            TypeConverter valueConverter, Function<V, V> valueFilter) {
        PropertyConfig<String, V> propertyConfig = createPropertyConfig(key, valueType, defaultValue, valueConverter,
                valueFilter);
        return _manager.getProperty(propertyConfig);
    }

    public <V> V getPropertyValue(String key, Class<V> valueType, V defaultValue, TypeConverter valueConverter,
            Function<V, V> valueFilter) {
        PropertyConfig<String, V> propertyConfig = createPropertyConfig(key, valueType, defaultValue, valueConverter,
                valueFilter);
        return _manager.getPropertyValue(propertyConfig);
    }

    protected <V> PropertyConfig<String, V> createPropertyConfig(String key, Class<V> valueType, V defaultValue,
            TypeConverter valueConverter, Function<V, V> valueFilter) {
        return ConfigurationProperties.<String, V> newConfigBuilder().setKey(key).setValueType(valueType)
                .setDefaultValue(defaultValue).addValueConverter(valueConverter).setValueFilter(valueFilter).build();
    }

}
