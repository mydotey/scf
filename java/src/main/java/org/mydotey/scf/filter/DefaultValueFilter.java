package org.mydotey.scf.filter;

import org.mydotey.java.ObjectExtension;

public class DefaultValueFilter<T> extends AbstractValueFilter<T> {

    private T _defaultValue;

    public DefaultValueFilter(T defaultValue) {
        ObjectExtension.requireNonNullOrEmpty(defaultValue, "defaultValue");
        _defaultValue = defaultValue;
    }

    @Override
    public T apply(T t) {
        return ObjectExtension.isNullOrEmpty(t) ? _defaultValue : t;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_defaultValue == null) ? 0 : _defaultValue.hashCode());
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
        DefaultValueFilter other = (DefaultValueFilter) obj;
        if (_defaultValue == null) {
            if (other._defaultValue != null)
                return false;
        } else if (!_defaultValue.equals(other._defaultValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DefaultValueFilter [defaultValue=" + _defaultValue + "]";
    }

}
