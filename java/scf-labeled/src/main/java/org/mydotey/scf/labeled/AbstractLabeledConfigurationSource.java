package org.mydotey.scf.labeled;

import java.util.Collections;

import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.facade.LabeledProperties;

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> config) {
        PropertyConfig<?, V> rawConfig = config;
        if (config.getKey() instanceof LabeledKey)
            rawConfig = LabeledProperties.removeLabels((PropertyConfig) config);

        return getPropertyValue(rawConfig, Collections.emptyList());
    }

}
