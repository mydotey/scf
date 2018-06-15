package org.mydotey.scf.type;

import java.util.Objects;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public abstract class AbstractTypeConverter<S, T> implements TypeConverter<S, T> {

    private Class<S> _sourceType;
    private Class<T> _targetType;

    public AbstractTypeConverter(Class<S> sourceType, Class<T> targetType) {
        Objects.requireNonNull(sourceType, "sourceType is null");
        Objects.requireNonNull(targetType, "targetType is null");
        _sourceType = sourceType;
        _targetType = targetType;
    }

    @Override
    public Class<S> getSourceType() {
        return _sourceType;
    }

    @Override
    public Class<T> getTargetType() {
        return _targetType;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        AbstractTypeConverter other = (AbstractTypeConverter) obj;
        return Objects.equals(_sourceType, other._sourceType) && Objects.equals(_targetType, other._targetType);
    }

    @Override
    public String toString() {
        return String.format("%s { sourceType: %s, targetType: %s }", getClass().getSimpleName(), _sourceType,
                _targetType);
    }

}
