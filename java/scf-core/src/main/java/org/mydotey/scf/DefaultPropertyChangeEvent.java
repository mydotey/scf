package org.mydotey.scf;

/**
 * @author koqizhao
 *
 * Jul 19, 2018
 */
public class DefaultPropertyChangeEvent<K, V> implements PropertyChangeEvent<K, V> {

    private Property<K, V> _property;
    private V _oldValue;
    private V _newValue;
    private long _changeTime;

    public DefaultPropertyChangeEvent(Property<K, V> property, V oldValue, V newValue) {
        this(property, oldValue, newValue, System.currentTimeMillis());
    }

    public DefaultPropertyChangeEvent(Property<K, V> property, V oldValue, V newValue, long changeTime) {
        _property = property;
        _oldValue = oldValue;
        _newValue = newValue;
        _changeTime = changeTime;
    }

    @Override
    public Property<K, V> getProperty() {
        return _property;
    }

    @Override
    public V getOldValue() {
        return _oldValue;
    }

    @Override
    public V getNewValue() {
        return _newValue;
    }

    @Override
    public long getChangeTime() {
        return _changeTime;
    }

    @Override
    public String toString() {
        return String.format("%s { property: %s, oldValue: %s, newValue: %s, changeTime: %s }",
                getClass().getSimpleName(), _property, _oldValue, _newValue, _changeTime);
    }

}
