package org.mydotey.scf;

import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public interface ConfigurationSource {

    ConfigurationSourceConfig getConfig();

    <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig);

    boolean isDynamic();

    void addChangeListener(Consumer<ConfigurationSource> changeListener);

}
