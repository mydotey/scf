package org.mydotey.scf.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringToListConverter<V> extends StringConverter<List<V>> {

    private StringConverter<V> _typeConverter;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public StringToListConverter(StringConverter<V> typeConverter) {
        super((Class) List.class);
        Objects.requireNonNull(typeConverter, "typeConverter is null");
        _typeConverter = typeConverter;
    }

    @Override
    public List<V> convert(String source) {
        if (source == null)
            return null;

        source = source.trim();
        if (source.isEmpty())
            return null;

        List<V> list = null;
        String[] array = source.split(",");
        for (String str : array) {
            if (str == null)
                continue;

            str = str.trim();
            if (str.isEmpty())
                continue;

            V v = _typeConverter.convert(str);
            if (v == null)
                continue;

            if (list == null)
                list = new ArrayList<>();

            list.add(v);
        }

        return list;
    }

}
