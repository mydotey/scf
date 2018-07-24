package org.mydotey.scf.labeled;

import java.util.Collection;
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

        return super.getPropertyValue(noLabelConfig);
    }

    @Override
    protected Object getPropertyValue(Object key) {
        return getPropertyValue(key, Collections.emptyList());
    }

    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> noLabelConfig, Collection<PropertyLabel> labels) {
        Object value = getPropertyValue(noLabelConfig.getKey(), labels);
        return convert(noLabelConfig, value);
    }

    protected abstract Object getPropertyValue(Object key, Collection<PropertyLabel> labels);

}
