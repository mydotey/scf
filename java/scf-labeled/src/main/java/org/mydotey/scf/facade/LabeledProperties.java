package org.mydotey.scf.facade;

import java.util.Collection;

import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.labeled.DefaultLabeledKey;
import org.mydotey.scf.labeled.DefaultPropertyLabel;
import org.mydotey.scf.labeled.DefaultPropertyLabels;
import org.mydotey.scf.labeled.LabeledKey;
import org.mydotey.scf.labeled.PropertyLabel;
import org.mydotey.scf.labeled.PropertyLabels;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public class LabeledProperties {

    protected LabeledProperties() {

    }

    public static <K> LabeledKey.Builder<K> newKeyBuilder() {
        return new DefaultLabeledKey.Builder<>();
    }

    public static PropertyLabel newLabel(Object key, Object value) {
        return new DefaultPropertyLabel(key, value);
    }

    public static PropertyLabels newLabels(Collection<PropertyLabel> labels) {
        return newLabels(labels, null);
    }

    public static PropertyLabels newLabels(Collection<PropertyLabel> labels, PropertyLabels alternative) {
        return new DefaultPropertyLabels(labels, alternative);
    }

    public static <K, V> PropertyConfig<K, V> removeLabels(PropertyConfig<LabeledKey<K>, V> config) {
        return ConfigurationProperties.<K, V> newConfigBuilder().setKey(config.getKey().getKey())
                .setValueType(config.getValueType()).setDefaultValue(config.getDefaultValue())
                .addValueConverters(config.getValueConverters()).setValueFilter(config.getValueFilter()).build();
    }

}
