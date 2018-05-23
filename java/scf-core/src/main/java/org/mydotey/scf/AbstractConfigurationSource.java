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
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig) {
        try {
            return doGetPropertyValue(propertyConfig);
        } catch (Exception e) {
            LOGGER.error("source getPropertyValue failed to run", e);
            return null;
        }
    }

    protected abstract <K, V> V doGetPropertyValue(PropertyConfig<K, V> propertyConfig);

    @Override
    public void addChangeListener(Consumer<ConfigurationSource> changeListener) {
        Objects.requireNonNull("changeListener", "changeListener is null");

        synchronized (this) {
            if (_changeListeners == null)
                _changeListeners = new ArrayList<>();

            _changeListeners.add(changeListener);
        }
    }

    protected void raiseChangeEvent() {
        synchronized (this) {
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

    @Override
    public String toString() {
        return String.format("{ type: %s, config: %s }", getClass(), getConfig());
    }

}
