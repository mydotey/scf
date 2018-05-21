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

}
