package org.mydotey.scf.labeled;

import java.util.Collections;

import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.PropertyConfig;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public abstract class AbstractLabeledConfigurationSource extends AbstractConfigurationSource
        implements LabeledConfigurationSource {

    public AbstractLabeledConfigurationSource(ConfigurationSourceConfig config) {
        super(config);
    }

    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> config) {
        return getPropertyValue(config, Collections.<PropertyLabel> emptyList());
    }

}
