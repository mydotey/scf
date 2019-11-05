package org.mydotey.scf;

import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public interface Property<K, V> {

    /**
     * @see PropertyConfig
     */
    PropertyConfig<K, V> getConfig();

    /**
     * property value, if not configured or no valid value, default to defaultValue Of PropertyConfig
     * <p>
     * @see PropertyConfig#getDefaultValue()
     */
    V getValue();

    /**
     * which configuration source is actually used
     * return null if using default value
     */
    ConfigurationSource getSource();

    /**
     * listeners to the value change, notified once value changed
     */
    void addChangeListener(Consumer<PropertyChangeEvent<K, V>> changeListener);

}