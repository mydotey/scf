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
public abstract class AbstractConfigurationSource implements ConfigurationSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurationSource.class);

    private ConfigurationSourceConfig _config;

    private volatile List<Consumer<ConfigurationSource>> _changeListeners;

    public AbstractConfigurationSource(ConfigurationSourceConfig config) {
        Objects.requireNonNull(config, "config is null");

        _config = config;
    }

    @Override
    public ConfigurationSourceConfig getConfig() {
        return _config;
    }

    @Override
    public <K, V> V getPropertyValue(K key, Class<V> valueType) {
        if (key == null)
            return null;

        if (!isSupported(key, valueType))
            return null;

        try {
            return doGetPropertyValue(key, valueType);
        } catch (Exception e) {
            LOGGER.error("source getPropertyValue failed to run", e);
            return null;
        }
    }

    protected abstract <K, V> boolean isSupported(K key, Class<V> valueType);

    protected abstract <K, V> V doGetPropertyValue(K key, Class<V> valueType);

    @Override
    public synchronized void addChangeListener(Consumer<ConfigurationSource> changeListener) {
        Objects.requireNonNull("changeListener", "changeListener is null");

        if (_changeListeners == null)
            _changeListeners = new ArrayList<>();
        _changeListeners.add(changeListener);
    }

    protected synchronized void raiseChangeEvent() {
        if (_changeListeners == null)
            return;

        _changeListeners.forEach(l -> {
            try {
                l.accept(AbstractConfigurationSource.this);
            } catch (Exception e) {
                LOGGER.error("source change listener failed to run", e);
            }
        });
    }

}
