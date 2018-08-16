package org.mydotey.scf;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.mydotey.scf.type.TypeConverter;
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

    private volatile List<Consumer<ConfigurationSourceChangeEvent>> _changeListeners;

    public AbstractConfigurationSource(ConfigurationSourceConfig config) {
        Objects.requireNonNull(config, "config is null");

        _config = config;
    }

    @Override
    public ConfigurationSourceConfig getConfig() {
        return _config;
    }

    @Override
    public void addChangeListener(Consumer<ConfigurationSourceChangeEvent> changeListener) {
        Objects.requireNonNull(changeListener, "changeListener is null");

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
                    l.accept(new DefaultConfigurationSourceChangeEvent(AbstractConfigurationSource.this));
                } catch (Exception e) {
                    LOGGER.error("source change listener failed to run", e);
                }
            });
        }
    }

    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig) {
        Object value = getPropertyValue(propertyConfig.getKey());
        return convert(propertyConfig, value);
    }

    protected abstract Object getPropertyValue(Object key);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <K, V> V convert(PropertyConfig<K, V> propertyConfig, Object value) {
        if (isNull(value))
            return null;

        if (propertyConfig.getValueType().isAssignableFrom(value.getClass()))
            return (V) value;

        if (propertyConfig.getValueConverters() == null)
            return null;

        for (TypeConverter<?, ?> typeConverter : propertyConfig.getValueConverters()) {
            if (typeConverter.getSourceType().isAssignableFrom(value.getClass())
                    && propertyConfig.getValueType().isAssignableFrom(typeConverter.getTargetType()))
                return (V) ((TypeConverter) typeConverter).convert(value);
        }

        return null;
    }

    @SuppressWarnings("rawtypes")
    protected boolean isNull(Object value) {
        if (value == null)
            return true;

        if (value instanceof String)
            return ((String) value).trim().isEmpty();

        if (value instanceof Collection)
            return ((Collection) value).isEmpty();

        if (value instanceof Map)
            return ((Map) value).isEmpty();

        if (value.getClass().isArray())
            return Array.getLength(value) == 0;

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s { config: %s, changeListeners: %s }", getClass().getSimpleName(), getConfig(),
                _changeListeners);
    }

}
