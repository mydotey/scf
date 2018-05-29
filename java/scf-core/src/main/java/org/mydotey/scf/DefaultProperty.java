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

    private PropertyConfig<K, V> _config;
    private volatile V _value;
    private volatile List<Consumer<Property<K, V>>> _changeListeners;

    public DefaultProperty(PropertyConfig<K, V> config, V value) {
        Objects.requireNonNull(config, "config is null");

        _config = config;
        _value = value;
    }

    @Override
    public PropertyConfig<K, V> getConfig() {
        return _config;
    }

    @Override
    public V getValue() {
        return _value;
    }

    public void setValue(V value) {
        _value = value;
    }

    @Override
    public synchronized void addChangeListener(Consumer<Property<K, V>> changeListener) {
        Objects.requireNonNull("changeListener", "changeListener is null");

        if (_changeListeners == null)
            _changeListeners = new ArrayList<>();
        _changeListeners.add(changeListener);
    }

    public synchronized void raiseChangeEvent() {
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

    @Override
    public String toString() {
        return String.format("%s { config: %s, value: %s, changeListeners: %s }", getClass().getSimpleName(), _config,
                _value, _changeListeners);
    }

}
