package org.mydotey.scf.filter;

import org.mydotey.java.ObjectExtension;

/**
 * Created by Qiang Zhao on 10/05/2016.
 */
public class RangeValueFilter<T extends Comparable<T>> extends AbstractValueFilter<T> {

    private T _lowerBound;
    private T _upperBound;

    public RangeValueFilter(T lowerBound, T upperBound) {
        ObjectExtension.requireNonNull(lowerBound, "lowerBound");
        ObjectExtension.requireNonNull(upperBound, "upperBound");
        _lowerBound = lowerBound;
        _upperBound = upperBound;
    }

    public T lowerBound() {
        return _lowerBound;
    }

    public T upperBound() {
        return _upperBound;
    }

    @Override
    public T apply(T t) {
        if (t == null)
            return null;

        return t.compareTo(_lowerBound) >= 0 && t.compareTo(_upperBound) <= 0 ? t : null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        RangeValueFilter other = (RangeValueFilter) obj;
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
        return "RangeValueFilter [lowerBound=" + _lowerBound + ",  upperBound=" + _upperBound + "]";
    }

}
