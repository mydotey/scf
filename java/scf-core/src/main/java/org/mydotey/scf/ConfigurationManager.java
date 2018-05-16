package org.mydotey.scf;

import java.util.Collection;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public interface ConfigurationManager {

    Collection<ConfigurationSource> sources();

    @SuppressWarnings("rawtypes")
    Collection<Property> properties();

    <K, V> Property<K, V> getProperty(K key, Class<V> valueClazz);

}
