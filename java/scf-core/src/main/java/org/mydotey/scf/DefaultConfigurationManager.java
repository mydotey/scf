package org.mydotey.scf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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

        return s1.getConfig().getPriority() > s2.getConfig().getPriority() ? -1 : 1;
    };

    private ConfigurationManagerConfig _config;
    private List<ConfigurationSource> _sortedSources;

    private ConcurrentHashMap<Object, DefaultProperty> _properties;
    private Object _propertiesLock;

    public DefaultConfigurationManager(ConfigurationManagerConfig config) {
        Objects.requireNonNull(config, "config is null");

        _config = config;

        _sortedSources = new ArrayList<>(_config.getSources());
        Collections.sort(_sortedSources, SOURCE_COMPARATOR);
        _sortedSources.forEach(s -> s.addChangeListener(this::onSourceChange));

        StringBuilder message = new StringBuilder();
        message.append("Configuration Manager ").append(_config.getName()).append(" inited with ")
                .append(_sortedSources.size()).append(" sources\n");
        _sortedSources.forEach(s -> message.append(s).append("\n"));
        LOGGER.info(message.toString());

        _properties = new ConcurrentHashMap<>();
        _propertiesLock = new Object();
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
    public <K, V> Property<K, V> getProperty(PropertyConfig<K, V> propertyConfig) {
        Objects.requireNonNull(propertyConfig, "propertyConfig is null");

        Property<K, V> property = doGetProperty(propertyConfig);
        if (!Objects.equals(property.getConfig(), propertyConfig))
            throw new IllegalArgumentException(String.format(
                    "make sure using same config for property: %s, previous config: %s, current Config: %s",
                    propertyConfig.getKey(), property.getConfig(), propertyConfig));

        return property;
    }

    protected <K, V> Property<K, V> doGetProperty(PropertyConfig<K, V> propertyConfig) {
        DefaultProperty<K, V> property = _properties.get(propertyConfig.getKey());
        if (property != null)
            return property;

        synchronized (_propertiesLock) {
            property = _properties.get(propertyConfig.getKey());
            if (property != null)
                return property;

            V value = getPropertyValue(propertyConfig);
            property = newProperty(propertyConfig, value);
            _properties.put(propertyConfig.getKey(), property);
            return property;
        }
    }

    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig) {
        Objects.requireNonNull(propertyConfig, "propertyConfig is null");

        V value = null;
        for (ConfigurationSource source : _sortedSources) {
            value = source.getPropertyValue(propertyConfig);

            value = filterValue(propertyConfig, value);

            if (value != null)
                break;
        }

        return value == null ? propertyConfig.getDefaultValue() : value;
    }

    protected <K, V> V filterValue(PropertyConfig<K, V> config, V value) {
        if (value == null)
            return value;

        if (config.getValueFilter() == null)
            return value;

        try {
            value = config.getValueFilter().apply(value);
        } catch (Exception e) {
            LOGGER.error("failed to run valueFilter: " + config.getValueFilter(), e);
        }

        return value;
    }

    protected <K, V> DefaultProperty<K, V> newProperty(PropertyConfig<K, V> config, V value) {
        return new DefaultProperty<K, V>(config, value);
    }

    protected void onSourceChange(ConfigurationSource source) {
        synchronized (_propertiesLock) {
            _properties.values().forEach(p -> {
                Object value = getPropertyValue(p.getConfig());
                if (Objects.equals(p.getValue(), value))
                    return;

                p.setValue(value);

                _config.getTaskExecutor().accept(p::raiseChangeEvent);
            });
        }
    }

}
