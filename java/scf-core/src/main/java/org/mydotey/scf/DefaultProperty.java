package org.mydotey.scf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public class DefaultProperty<K, V> implements Property<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProperty.class);

    private K _key;
    private volatile V _value;
    private Class<V> _valueClazz;

    private volatile List<Consumer<Property<K, V>>> _changeListeners;

    public DefaultProperty(K key, V value, Class<V> valueClazz) {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(valueClazz, "valueClazz is null");

        _key = key;
        _value = value;
        _valueClazz = valueClazz;
    }

    @Override
    public K key() {
        return _key;
    }

    @Override
    public V value() {
        return _value;
    }

    @Override
    public String toString() {
        return String.format("{ key: %s, value: %s }", _key, _value);
    }

    @Override
    public synchronized void addChangeListener(Consumer<Property<K, V>> changeListener) {
        if (_changeListeners == null)
            _changeListeners = new ArrayList<>();

        Objects.requireNonNull("changeListener", "changeListener is null");
        _changeListeners.add(changeListener);
    }

    public Class<V> valueClazz() {
        return _valueClazz;
    }

    public synchronized void updateValue(V value) {
        _value = value;

        raiseChangeEvent();
    }

    protected void raiseChangeEvent() {
        if (_changeListeners == null)
            return;

        _changeListeners.forEach(l -> {
            try {
                l.accept(DefaultProperty.this);
            } catch (Exception e) {
                LOGGER.error("property change listener failed to run", e);
            }
        });
    }

}
