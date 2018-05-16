package org.mydotey.scf;

import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public interface ConfigurationSource {

    String name();

    int priority();

    <K, V> V getPropertyValue(K key, Class<V> valueClazz);

    void addChangeListener(Consumer<ConfigurationSource> changeListener);

}
