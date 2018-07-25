package org.mydotey.scf.facade;

import java.util.Arrays;
import java.util.Collection;

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
public class LabeledConfigurationProperties {

    protected LabeledConfigurationProperties() {

    }

    public static <K> LabeledKey.Builder<K> newKeyBuilder() {
        return new DefaultLabeledKey.Builder<>();
    }

    public static PropertyLabel newLabel(Object key, Object value) {
        return new DefaultPropertyLabel(key, value);
    }

    public static PropertyLabels newLabels(PropertyLabel... labels) {
        return newLabels(Arrays.asList(labels), null);
    }

    public static PropertyLabels newLabels(Collection<PropertyLabel> labels) {
        return newLabels(labels, null);
    }

    public static PropertyLabels newLabels(Collection<PropertyLabel> labels, PropertyLabels alternative) {
        return new DefaultPropertyLabels(labels, alternative);
    }

}
