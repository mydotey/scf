package org.mydotey.scf;

import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public interface Property<K, V> {

    K key();

    V value();

    void addChangeListener(Consumer<Property<K, V>> changeListener);

}