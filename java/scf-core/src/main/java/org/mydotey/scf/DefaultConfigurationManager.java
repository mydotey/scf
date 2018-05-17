package org.mydotey.scf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultConfigurationManager implements ConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigurationManager.class);

    protected static final Comparator<ConfigurationSource> SOURCE_COMPARATOR = (s1, s2) -> {
        Objects.requireNonNull(s1, "s1 is null");
        Objects.requireNonNull(s2, "s2 is null");

        if (s1.getConfig().getPriority() == s2.getConfig().getPriority())
            throw new IllegalArgumentException("2 sources have the same priority. s1: " + s1.getConfig().getName()
                    + ", s2: " + s2.getConfig().getName());

        return s1.getConfig().getPriority() > s2.getConfig().getPriority() ? 1 : -1;
    };

    private ConfigurationManagerConfig _config;
    private List<ConfigurationSource> _sortedSources;

    private ConcurrentHashMap<Object, DefaultProperty> _properties;

    private Object _propertyGetLock = new Object();
    private ExecutorService _changeHandlerThreadPool;

    public DefaultConfigurationManager(ConfigurationManagerConfig config) {
        Objects.requireNonNull(config, "config is null");

        _config = config;

        _sortedSources = new ArrayList<>(_config.getSources());
        Collections.sort(_sortedSources, SOURCE_COMPARATOR);
        _sortedSources.forEach(s -> s.addChangeListener(this::onSourceChange));

        StringBuilder message = new StringBuilder();
        message.append("Configuration Manager ").append(_config.getName()).append(" inited with ")
                .append(_sortedSources.size()).append(" sources\n");
        _sortedSources.forEach(s -> message.append("priority: ").append(s.getConfig().getPriority())
                .append(", source: ").append(s.getConfig().getName()).append("\n"));
        LOGGER.info(message.toString());

        _properties = new ConcurrentHashMap<>();

        _propertyGetLock = new Object();
        if (config.getChangeHandlerThreadPoolSize() > 0)
            _changeHandlerThreadPool = Executors.newFixedThreadPool(config.getChangeHandlerThreadPoolSize(), r -> {
                Thread thread = r == null ? new Thread() : new Thread(r);
                thread.setDaemon(true);
                thread.setName("changeHandlerThread-" + config.getName());
                return thread;
            });
    }

    @Override
    public ConfigurationManagerConfig getConfig() {
        return _config;
    }

    @Override
    public Collection<Property> getProperties() {
        return (Collection) _properties.values();
    }

    @Override
    public <K, V> Property<K, V> getProperty(PropertyConfig<K, V> config) {
        Objects.requireNonNull(config, "config is null");

        Property<K, V> property = _properties.get(config.getKey());
        if (property == null) {
            synchronized (_propertyGetLock) {
                property = _properties.get(config.getKey());
                if (property == null) {
                    V value = getPropertyValue(config);
                    property = newProperty(config, value);
                    _properties.put((Object) config.getKey(), (DefaultProperty) property);
                }
            }
        }

        if (property.getConfig().getValueType() != config.getValueType())
            throw new IllegalArgumentException("a property with the same key exists, but with a different valueType: "
                    + property.getConfig().getValueType() + ", maybe the valueClazz parameter is something wrong: "
                    + config.getValueType());

        return property;
    }

    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> config) {
        V value = null;
        for (ConfigurationSource source : _sortedSources) {
            value = source.getPropertyValue(config.getKey(), config.getValueType());

            filterValue(config, value);

            if (value != null)
                break;
        }

        return value == null ? config.getDefaultValue() : value;
    }

    protected <K, V> V filterValue(PropertyConfig<K, V> config, V value) {
        if (value == null)
            return value;

        Collection<Function<V, V>> valueFilters = config.getValueFilters();
        if (valueFilters == null || valueFilters.isEmpty())
            return value;

        for (Function<V, V> valueFilter : valueFilters) {
            try {
                value = valueFilter.apply(value);
                if (value == null)
                    break;
            } catch (Exception e) {
                LOGGER.error("failed to run valueFilter: " + valueFilter, e);
            }
        }

        return value;
    }

    protected <K, V> DefaultProperty<K, V> newProperty(PropertyConfig<K, V> config, V value) {
        return new DefaultProperty<K, V>(config, value);
    }

    protected void onSourceChange(ConfigurationSource source) {
        synchronized (_propertyGetLock) {
            _properties.values().forEach(p -> {
                Object value = getPropertyValue(p.getConfig());
                if (Objects.equals(p.getValue(), value))
                    return;

                p.setValue(value);

                if (_changeHandlerThreadPool == null)
                    p.raiseChangeEvent();
                else
                    _changeHandlerThreadPool.submit(() -> p.raiseChangeEvent());
            });
        }
    }

    @Override
    public void close() {
        if (_changeHandlerThreadPool != null)
            _changeHandlerThreadPool.shutdown();
    }

}
