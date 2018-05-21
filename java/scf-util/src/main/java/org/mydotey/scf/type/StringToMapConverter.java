package org.mydotey.scf.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringToMapConverter<K, V> extends StringConverter<Map<K, V>> {

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

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof StringToMapConverter))
            return false;

        StringToMapConverter other = (StringToMapConverter) obj;
        return Objects.equals(_keyConverter, other._valueConverter)
                && Objects.equals(_valueConverter, other._valueConverter);
    }

    @Override
    public String toString() {
        return String.format("{ converter: %s, KeyConverter: %s, valueConverter: %s }", getClass(), _keyConverter,
                _valueConverter);
    }

}
