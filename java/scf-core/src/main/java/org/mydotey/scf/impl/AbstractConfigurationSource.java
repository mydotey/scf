package org.mydotey.scf.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.PropertyConfig;
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

    private boolean _dynamic;
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
    public boolean isDynamic() {
        return _dynamic;
    }

    protected void setDynamic(boolean dynamic) {
        _dynamic = dynamic;
    }

    @Override
    public void addChangeListener(Consumer<ConfigurationSource> changeListener) {
        Objects.requireNonNull("changeListener", "changeListener is null");

        if (!_dynamic)
            return;

        synchronized (this) {
            if (_changeListeners == null)
                _changeListeners = new ArrayList<>();

            _changeListeners.add(changeListener);
        }
    }

    protected void raiseChangeEvent() {
        if (!_dynamic)
            return;

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

}
