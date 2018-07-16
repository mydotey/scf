package org.mydotey.scf.labeled;

import java.util.Collection;

import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.PropertyConfig;

/**
 * @author koqizhao
 *
 * Jun 15, 2018
 */
public interface LabeledConfigurationSource extends ConfigurationSource {

    /**
     * @param noLabelConfig normal config with plain key (non-labeled-key)
     */
    <K, V> V getPropertyValue(PropertyConfig<K, V> noLabelConfig, Collection<PropertyLabel> labels);

}
