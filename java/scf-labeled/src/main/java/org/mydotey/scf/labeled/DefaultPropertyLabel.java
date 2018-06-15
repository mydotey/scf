package org.mydotey.scf.labeled;

import java.util.Objects;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public class DefaultPropertyLabel implements PropertyLabel {

    private Object _key;
    private Object _value;

    public DefaultPropertyLabel(Object key, Object value) {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(value, "value is null");

        _key = key;
        _value = value;
    }

    @Override
    public Object getKey() {
        return _key;
    }

    @Override
    public Object getValue() {
        return _value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_key == null) ? 0 : _key.hashCode());
        result = prime * result + ((_value == null) ? 0 : _value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        DefaultPropertyLabel label = (DefaultPropertyLabel) obj;

        if (!Objects.equals(_key, label._key))
            return false;

        if (!Objects.equals(_value, label._value))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return String.format("%s { key: %s, value: %s }", getClass().getSimpleName(), _key, _value);
    }

}
