package org.mydotey.scf.type;

import java.util.Objects;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public abstract class StringConverter<V> implements TypeConverter<String, V> {

    private Class<V> _targetType;

    public StringConverter(Class<V> targetType) {
        Objects.requireNonNull(targetType, "typeType is null");
        _targetType = targetType;
    }

    @Override
    public Class<String> getSourceType() {
        return String.class;
    }

    @Override
    public Class<V> getTargetType() {
        return _targetType;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof StringConverter))
            return false;

        StringConverter other = (StringConverter) obj;
        return Objects.equals(_targetType, other._targetType);
    }

    @Override
    public String toString() {
        return String.format("{ converter: %s, targetType: %s }", getClass(), _targetType);
    }

}
