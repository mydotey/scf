package org.mydotey.scf;

/**
 * @author koqizhao
 *
 * Jul 19, 2018
 */
public interface PropertyChangeEvent<K, V> {

    Property<K, V> getProperty();

    V getOldValue();

    V getNewValue();

    long getChangeTime();

}
