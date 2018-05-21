package org.mydotey.scf.type;

import java.util.Objects;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class InplaceConverter<V> implements TypeConverter<V, V> {

    private Class<V> _type;

    public InplaceConverter(Class<V> type) {
        Objects.requireNonNull(type, "type is null");
        _type = type;
    }

    @Override
    public Class<V> getSourceType() {
        return _type;
    }

    @Override
    public Class<V> getTargetType() {
        return _type;
    }

    @Override
    public V convert(V s) {
        return s;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof InplaceConverter))
            return false;

        InplaceConverter other = (InplaceConverter) obj;
        return Objects.equals(_type, other._type);
    }

    @Override
    public String toString() {
        return String.format("{ converter: %s, type: %s }", getClass(), _type);
    }

}
