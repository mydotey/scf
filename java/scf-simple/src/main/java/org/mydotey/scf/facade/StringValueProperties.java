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
public class StringValueProperties<K> {

    private ConfigurationManager _manager;

    public StringValueProperties(ConfigurationManager manager) {
        Objects.requireNonNull(manager, "manager is null");
        _manager = manager;
    }

    public ConfigurationManager getManager() {
        return _manager;
    }

    public Property<K, String> getStringProperty(K key) {
        return getStringProperty(key, null);
    }

    public String getStringPropertyValue(K key) {
        return getStringPropertyValue(key, null);
    }

    public Property<K, String> getStringProperty(K key, String defaultValue) {
        return getStringProperty(key, defaultValue, null);
    }

    public String getStringPropertyValue(K key, String defaultValue) {
        return getStringPropertyValue(key, defaultValue, null);
    }

    public Property<K, String> getStringProperty(K key, String defaultValue, Function<String, String> valueFilter) {
        return getProperty(key, String.class, defaultValue, StringInplaceConverter.DEFAULT, valueFilter);
    }

    public String getStringPropertyValue(K key, String defaultValue, Function<String, String> valueFilter) {
        return getPropertyValue(key, String.class, defaultValue, StringInplaceConverter.DEFAULT, valueFilter);
    }

    public Property<K, Integer> getIntProperty(K key) {
        return getIntProperty(key, null);
    }

    public Integer getIntPropertyValue(K key) {
        return getIntPropertyValue(key, null);
    }

    public Property<K, Integer> getIntProperty(K key, Integer defaultValue) {
        return getIntProperty(key, defaultValue, null);
    }

    public Integer getIntPropertyValue(K key, Integer defaultValue) {
        return getIntPropertyValue(key, defaultValue, null);
    }

    public Property<K, Integer> getIntProperty(K key, Integer defaultValue, Function<Integer, Integer> valueFilter) {
        return getProperty(key, Integer.class, defaultValue, StringToIntConverter.DEFAULT, valueFilter);
    }

    public Integer getIntPropertyValue(K key, Integer defaultValue, Function<Integer, Integer> valueFilter) {
        return getPropertyValue(key, Integer.class, defaultValue, StringToIntConverter.DEFAULT, valueFilter);
    }

    public Property<K, Long> getLongProperty(K key) {
        return getLongProperty(key, null);
    }

    public Long getLongPropertyValue(K key) {
        return getLongPropertyValue(key, null);
    }

    public Property<K, Long> getLongProperty(K key, Long defaultValue) {
        return getLongProperty(key, defaultValue, null);
    }

    public Long getLongPropertyValue(K key, Long defaultValue) {
        return getLongPropertyValue(key, defaultValue, null);
    }

    public Property<K, Long> getLongProperty(K key, Long defaultValue, Function<Long, Long> valueFilter) {
        return getProperty(key, Long.class, defaultValue, StringToLongConverter.DEFAULT, valueFilter);
    }

    public Long getLongPropertyValue(K key, Long defaultValue, Function<Long, Long> valueFilter) {
        return getPropertyValue(key, Long.class, defaultValue, StringToLongConverter.DEFAULT, valueFilter);
    }

    public Property<K, Float> getFloatProperty(K key) {
        return getFloatProperty(key, null);
    }

    public Float getFloatPropertyValue(K key) {
        return getFloatPropertyValue(key, null);
    }

    public Property<K, Float> getFloatProperty(K key, Float defaultValue) {
        return getFloatProperty(key, defaultValue, null);
    }

    public Float getFloatPropertyValue(K key, Float defaultValue) {
        return getFloatPropertyValue(key, defaultValue, null);
    }

    public Property<K, Float> getFloatProperty(K key, Float defaultValue, Function<Float, Float> valueFilter) {
        return getProperty(key, Float.class, defaultValue, StringToFloatConverter.DEFAULT, valueFilter);
    }

    public Float getFloatPropertyValue(K key, Float defaultValue, Function<Float, Float> valueFilter) {
        return getPropertyValue(key, Float.class, defaultValue, StringToFloatConverter.DEFAULT, valueFilter);
    }

    public Property<K, Double> getDoubleProperty(K key) {
        return getDoubleProperty(key, null);
    }

    public Double getDoublePropertyValue(K key) {
        return getDoublePropertyValue(key, null);
    }

    public Property<K, Double> getDoubleProperty(K key, Double defaultValue) {
        return getDoubleProperty(key, defaultValue, null);
    }

    public Double getDoublePropertyValue(K key, Double defaultValue) {
        return getDoublePropertyValue(key, defaultValue, null);
    }

    public Property<K, Double> getDoubleProperty(K key, Double defaultValue, Function<Double, Double> valueFilter) {
        return getProperty(key, Double.class, defaultValue, StringToDoubleConverter.DEFAULT, valueFilter);
    }

    public Double getDoublePropertyValue(K key, Double defaultValue, Function<Double, Double> valueFilter) {
        return getPropertyValue(key, Double.class, defaultValue, StringToDoubleConverter.DEFAULT, valueFilter);
    }

    public Property<K, Boolean> getBooleanProperty(K key) {
        return getBooleanProperty(key, null);
    }

    public Boolean getBooleanPropertyValue(K key) {
        return getBooleanPropertyValue(key, null);
    }

    public Property<K, Boolean> getBooleanProperty(K key, Boolean defaultValue) {
        return getBooleanProperty(key, defaultValue, null);
    }

    public Boolean getBooleanPropertyValue(K key, Boolean defaultValue) {
        return getBooleanPropertyValue(key, defaultValue, null);
    }

    public Property<K, Boolean> getBooleanProperty(K key, Boolean defaultValue,
            Function<Boolean, Boolean> valueFilter) {
        return getProperty(key, Boolean.class, defaultValue, StringToBooleanConverter.DEFAULT, valueFilter);
    }

    public Boolean getBooleanPropertyValue(K key, Boolean defaultValue, Function<Boolean, Boolean> valueFilter) {
        return getPropertyValue(key, Boolean.class, defaultValue, StringToBooleanConverter.DEFAULT, valueFilter);
    }

    public Property<K, List<String>> getListProperty(K key) {
        return getListProperty(key, (List<String>) null);
    }

    public List<String> getListPropertyValue(K key) {
        return getListPropertyValue(key, (List<String>) null);
    }

    public Property<K, List<String>> getListProperty(K key, List<String> defaultValue) {
        return getListProperty(key, defaultValue, StringInplaceConverter.DEFAULT);
    }

    public List<String> getListPropertyValue(K key, List<String> defaultValue) {
        return getListPropertyValue(key, defaultValue, StringInplaceConverter.DEFAULT);
    }

    public <V> Property<K, List<V>> getListProperty(K key, TypeConverter<String, V> typeConverter) {
        return getListProperty(key, null, typeConverter);
    }

    public <V> List<V> getListPropertyValue(K key, TypeConverter<String, V> typeConverter) {
        return getListPropertyValue(key, null, typeConverter);
    }

    public <V> Property<K, List<V>> getListProperty(K key, List<V> defaultValue,
            TypeConverter<String, V> typeConverter) {
        return getListProperty(key, defaultValue, typeConverter, null);
    }

    public <V> List<V> getListPropertyValue(K key, List<V> defaultValue, TypeConverter<String, V> typeConverter) {
        return getListPropertyValue(key, defaultValue, typeConverter, null);
    }

    public <V> Property<K, List<V>> getListProperty(K key, List<V> defaultValue, TypeConverter<String, V> typeConverter,
            Function<List<V>, List<V>> valueFilter) {
        return getProperty(key, (Class) List.class, defaultValue, new StringToListConverter(typeConverter),
                valueFilter);
    }

    public <V> List<V> getListPropertyValue(K key, List<V> defaultValue, TypeConverter<String, V> typeConverter,
            Function<List<V>, List<V>> valueFilter) {
        return getPropertyValue(key, (Class) List.class, defaultValue, new StringToListConverter(typeConverter),
                valueFilter);
    }

    public Property<K, Map<String, String>> getMapProperty(K key) {
        return getMapProperty(key, null);
    }

    public Map<String, String> getMapPropertyValue(K key) {
        return getMapPropertyValue(key, null);
    }

    public Property<K, Map<String, String>> getMapProperty(K key, Map<String, String> defaultValue) {
        return getMapProperty(key, defaultValue, StringInplaceConverter.DEFAULT, StringInplaceConverter.DEFAULT);
    }

    public Map<String, String> getMapPropertyValue(K key, Map<String, String> defaultValue) {
        return getMapPropertyValue(key, defaultValue, StringInplaceConverter.DEFAULT, StringInplaceConverter.DEFAULT);
    }

    public <MK, MV> Property<K, Map<MK, MV>> getMapProperty(K key, TypeConverter<String, MK> keyConverter,
            TypeConverter<String, MV> valueConverter) {
        return getMapProperty(key, null, keyConverter, valueConverter);
    }

    public <MK, MV> Map<MK, MV> getMapPropertyValue(K key, TypeConverter<String, MK> keyConverter,
            TypeConverter<String, MV> valueConverter) {
        return getMapPropertyValue(key, null, keyConverter, valueConverter);
    }

    public <MK, MV> Property<K, Map<MK, MV>> getMapProperty(K key, Map<MK, MV> defaultMValue,
            TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter) {
        return getMapProperty(key, defaultMValue, keyConverter, valueConverter, null);
    }

    public <MK, MV> Map<MK, MV> getMapPropertyValue(K key, Map<MK, MV> defaultMValue,
            TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter) {
        return getMapPropertyValue(key, defaultMValue, keyConverter, valueConverter, null);
    }

    public <MK, MV> Property<K, Map<MK, MV>> getMapProperty(K key, Map<MK, MV> defaultMValue,
            TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter,
            Function<Map<MK, MV>, Map<MK, MV>> valueFilter) {
        return getProperty(key, (Class) Map.class, defaultMValue,
                new StringToMapConverter(keyConverter, valueConverter), valueFilter);
    }

    public <MK, MV> Map<MK, MV> getMapPropertyValue(K key, Map<MK, MV> defaultValue,
            TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter,
            Function<Map<MK, MV>, Map<MK, MV>> valueFilter) {
        return getPropertyValue(key, (Class) Map.class, defaultValue,
                new StringToMapConverter(keyConverter, valueConverter), valueFilter);
    }

    public <V> Property<K, V> getProperty(K key, Class<V> valueType, V defaultValue, TypeConverter valueConverter,
            Function<V, V> valueFilter) {
        PropertyConfig<K, V> propertyConfig = createPropertyConfig(key, valueType, defaultValue, valueConverter,
                valueFilter);
        return _manager.getProperty(propertyConfig);
    }

    public <V> V getPropertyValue(K key, Class<V> valueType, V defaultValue, TypeConverter valueConverter,
            Function<V, V> valueFilter) {
        PropertyConfig<K, V> propertyConfig = createPropertyConfig(key, valueType, defaultValue, valueConverter,
                valueFilter);
        return _manager.getPropertyValue(propertyConfig);
    }

    protected <V> PropertyConfig<K, V> createPropertyConfig(K key, Class<V> valueType, V defaultValue,
            TypeConverter valueConverter, Function<V, V> valueFilter) {
        return ConfigurationProperties.<K, V> newConfigBuilder().setKey(key).setValueType(valueType)
                .setDefaultValue(defaultValue).addValueConverter(valueConverter).setValueFilter(valueFilter).build();
    }

}
