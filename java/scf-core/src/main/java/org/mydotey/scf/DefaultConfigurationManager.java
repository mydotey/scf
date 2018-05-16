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

        if (s1.priority() == s2.priority())
            throw new IllegalArgumentException(
                    "2 sources have the same priority. s1: " + s1.name() + ", s2: " + s2.name());

        return s1.priority() > s2.priority() ? 1 : -1;
    };

    private List<ConfigurationSource> _sources;

    private ConcurrentHashMap<Object, DefaultProperty> _properties;

    public DefaultConfigurationManager(Collection<ConfigurationSource> sources) {
        Objects.requireNonNull(sources, "sources is null");

        _sources = new ArrayList<>(sources);
        Collections.sort(_sources, SOURCE_COMPARATOR);
        _sources = Collections.unmodifiableList(_sources);

        _sources.forEach(s -> s.addChangeListener(this::onSourceChange));

        StringBuilder message = new StringBuilder();
        message.append("ConfigurationManager inited with ").append(_sources.size()).append(" sources\n");
        _sources.forEach(s -> message.append("priority: ").append(s.priority()).append(", source: ").append(s.name())
                .append("\n"));
        LOGGER.info(message.toString());

        _properties = new ConcurrentHashMap<>();
    }

    @Override
    public <K, V> Property<K, V> getProperty(K key, Class<V> valueClazz) {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(valueClazz, "valueClazz is null");

        DefaultProperty<K, V> property = _properties.computeIfAbsent(key, k -> {
            V value = getPropertyValue(key, valueClazz);
            return newProperty(key, value, valueClazz);
        });

        if (property.valueClazz() != valueClazz)
            throw new IllegalArgumentException("a property with the same key exists, but with a different valueClass: "
                    + property.valueClazz() + ", maybe the valueClazz parameter is something wrong: " + valueClazz);

        return property;
    }

    @Override
    public Collection<ConfigurationSource> sources() {
        return _sources;
    }

    @Override
    public Collection<Property> properties() {
        return Collections.unmodifiableCollection(_properties.values());
    }

    protected <K, V> DefaultProperty<K, V> newProperty(K key, V value, Class<V> valueClazz) {
        return new DefaultProperty<K, V>(key, value, valueClazz);
    }

    protected <K, V> V getPropertyValue(K key, Class<V> valueClazz) {
        V value = null;
        for (ConfigurationSource source : sources()) {
            value = source.getPropertyValue(key, valueClazz);
            if (value != null)
                break;
        }

        return value;
    }

    protected void onSourceChange(ConfigurationSource source) {
        _properties.values().forEach(p -> {
            Object value = getPropertyValue(p.key(), p.valueClazz());
            if (Objects.equals(p.value(), value))
                return;

            p.updateValue(value);
        });
    }

}
