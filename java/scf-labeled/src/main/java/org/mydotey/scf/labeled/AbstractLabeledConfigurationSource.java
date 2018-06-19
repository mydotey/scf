package org.mydotey.scf.labeled;

import java.util.Collections;

import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.facade.ConfigurationProperties;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public abstract class AbstractLabeledConfigurationSource extends AbstractConfigurationSource
        implements LabeledConfigurationSource {

    static <K, V> PropertyConfig<K, V> removeLabels(PropertyConfig<LabeledKey<K>, V> config) {
        return ConfigurationProperties.<K, V> newConfigBuilder().setKey(config.getKey().getKey())
                .setValueType(config.getValueType()).setDefaultValue(config.getDefaultValue())
                .addValueConverters(config.getValueConverters()).setValueFilter(config.getValueFilter()).build();
    }

    public AbstractLabeledConfigurationSource(ConfigurationSourceConfig config) {
        super(config);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> config) {
        PropertyConfig<?, V> noLabelConfig = config;
        if (config.getKey() instanceof LabeledKey)
            noLabelConfig = removeLabels((PropertyConfig) config);

        return getPropertyValue(noLabelConfig, Collections.emptyList());
    }

}
