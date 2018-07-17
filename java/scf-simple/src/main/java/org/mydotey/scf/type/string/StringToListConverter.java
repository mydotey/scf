package org.mydotey.scf.type.string;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringToListConverter<V> extends StringConverter<List<V>> {

    public static final StringToListConverter<String> DEFAULT = new StringToListConverter<>(
            StringInplaceConverter.DEFAULT);

    private TypeConverter<String, V> _typeConverter;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public StringToListConverter(TypeConverter<String, V> typeConverter) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((_typeConverter == null) ? 0 : _typeConverter.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        StringToListConverter other = (StringToListConverter) obj;
        return Objects.equals(_typeConverter, other._typeConverter);
    }

    @Override
    public String toString() {
        return String.format("%s { sourceType: %s, targetType: %s, typeConverter: %s }", getClass().getSimpleName(),
                getSourceType(), getTargetType(), _typeConverter);
    }

}
