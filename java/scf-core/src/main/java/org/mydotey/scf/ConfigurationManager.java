package org.mydotey.scf;

import java.util.Collection;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public interface ConfigurationManager {

    /**
     * @see ConfigurationManagerConfig
     */
    ConfigurationManagerConfig getConfig();

    /**
     * the properties created in @see {@link ConfigurationManager#getProperty(PropertyConfig)}
     */
    @SuppressWarnings("rawtypes")
    Collection<Property> getProperties();

    /**
     * get property value by @see {@link ConfigurationManager#getPropertyValue(PropertyConfig)},
     * and return a property with the propertyConfig and value
     * <p>
     * once a property is created, it is kept by the manager and will be auto-update after some configuration source changed
     * <p>
     * same propertyConfig in, same property out, 1 key 1 property
     */
    <K, V> Property<K, V> getProperty(PropertyConfig<K, V> propertyConfig);

    /**
     * get property value in each configuration source by source priority
     * <p>
     * if non-null value is got by a source @see {@link ConfigurationSource#getPropertyValue(PropertyConfig)},
     * if @see {@link PropertyConfig#getValueFilter()} is null, return the non-null value,
     * if @see {@link PropertyConfig#getValueFilter()} is non-null, apply the filter to the non-null value,
     * if the new value returned by the filter is non-null, return the new value.
     * otherwise, go to the next lower priority source
     * <p>
     * after handling all sources, no non-null value got, return null
     */
    <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig);

}
