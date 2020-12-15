package org.mydotey.scf.filter;

import org.mydotey.java.ObjectExtension;

/**
 * Created by Qiang Zhao on 10/05/2016.
 */
public class RangeValueConfig<T extends Comparable<T>> {

    private T _defaultValue;
    private T _lowerBound;
    private T _upperBound;

    public RangeValueConfig(T defaultValue, T lowerBound, T upperBound) {
        ObjectExtension.requireNonNull(defaultValue, "defaultValue");
        ObjectExtension.requireNonNull(lowerBound, "lowerBound");
        ObjectExtension.requireNonNull(upperBound, "upperBound");
        _defaultValue = defaultValue;
        _lowerBound = lowerBound;
        _upperBound = upperBound;
    }

    public T defaultValue() {
        return _defaultValue;
    }

    public T lowerBound() {
        return _lowerBound;
    }

    public T upperBound() {
        return _upperBound;
    }

    public DefaultValueFilter<T> toDefaultValueFilter() {
        return new DefaultValueFilter<T>(_defaultValue);
    }

    public RangeValueFilter<T> toRangeValueFilter() {
        return new RangeValueFilter<T>(_lowerBound, _upperBound);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_defaultValue == null) ? 0 : _defaultValue.hashCode());
        result = prime * result + ((_lowerBound == null) ? 0 : _lowerBound.hashCode());
        result = prime * result + ((_upperBound == null) ? 0 : _upperBound.hashCode());
        return result;
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
        RangeValueConfig other = (RangeValueConfig) obj;
        if (_defaultValue == null) {
            if (other._defaultValue != null)
                return false;
        } else if (!_defaultValue.equals(other._defaultValue))
            return false;
        if (_lowerBound == null) {
            if (other._lowerBound != null)
                return false;
        } else if (!_lowerBound.equals(other._lowerBound))
            return false;
        if (_upperBound == null) {
            if (other._upperBound != null)
                return false;
        } else if (!_upperBound.equals(other._upperBound))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RangeValueConfig [defaultValue=" + _defaultValue + ", lowerBound=" + _lowerBound + ", upperBound="
            + _upperBound + "]";
    }

}
