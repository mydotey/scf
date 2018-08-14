package org.mydotey.scf.type.string;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringToMapConverter<K, V> extends StringConverter<Map<K, V>> {

    public static final StringToMapConverter<String, String> DEFAULT = new StringToMapConverter<>(
            StringInplaceConverter.DEFAULT, StringInplaceConverter.DEFAULT);

    private TypeConverter<String, K> _keyConverter;
    private TypeConverter<String, V> _valueConverter;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public StringToMapConverter(TypeConverter<String, K> keyConverter, TypeConverter<String, V> valueConverter) {
        super((Class) Map.class);
        Objects.requireNonNull(keyConverter, "keyConverter is null");
        Objects.requireNonNull(valueConverter, "valueConverter is null");

        _keyConverter = keyConverter;
        _valueConverter = valueConverter;
    }

    @Override
    public Map<K, V> convert(String source) {
        if (source == null)
            return null;

        source = source.trim();
        if (source.isEmpty())
            return null;

        Map<K, V> map = null;
        String[] array = source.split(",");
        for (String str : array) {
            if (str == null)
                continue;

            str = str.trim();
            if (str.isEmpty())
                continue;

            String[] keyValueParts = str.split(":");
            if (keyValueParts.length != 2)
                continue;

            if (keyValueParts[0] == null || keyValueParts[1] == null)
                continue;

            String part1 = keyValueParts[0].trim();
            String part2 = keyValueParts[1].trim();
            if (part1.isEmpty() || part2.isEmpty())
                continue;

            K key = _keyConverter.convert(part1);
            V value = _valueConverter.convert(part2);
            if (key == null || value == null)
                continue;

            if (map == null)
                map = new HashMap<>();

            map.put(key, value);
        }

        return map;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_keyConverter == null) ? 0 : _keyConverter.hashCode());
        result = prime * result + ((_valueConverter == null) ? 0 : _valueConverter.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        StringToMapConverter other = (StringToMapConverter) obj;
        return Objects.equals(_keyConverter, other._keyConverter)
                && Objects.equals(_valueConverter, other._valueConverter);
    }

    @Override
    public String toString() {
        return String.format("%s { sourceType: %s, targetType: %s, keyConverter: %s, valueConverter: %s }",
                getClass().getSimpleName(), getSourceType(), getTargetType(), _keyConverter, _valueConverter);
    }

}
