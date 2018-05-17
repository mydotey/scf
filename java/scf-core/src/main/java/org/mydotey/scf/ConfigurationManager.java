package org.mydotey.scf;

import java.util.Collection;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public interface ConfigurationManager {

    ConfigurationManagerConfig getConfig();

    @SuppressWarnings("rawtypes")
    Collection<Property> getProperties();

    <K, V> Property<K, V> getProperty(PropertyConfig<K, V> config);

    <K, V> V getPropertyValue(PropertyConfig<K, V> config);

}
