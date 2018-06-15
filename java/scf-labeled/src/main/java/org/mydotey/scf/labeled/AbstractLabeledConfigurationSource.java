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

    public AbstractLabeledConfigurationSource(ConfigurationSourceConfig config) {
        super(config);
    }

    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> config) {
        PropertyConfig<?, V> propertyConfig = config;
        Collection<PropertyLabel> propertyLabels = Collections.<PropertyLabel> emptyList();
        if (config.getKey() instanceof LabeledKey) {
            propertyConfig = ConfigurationProperties.<Object, V> newConfigBuilder()
                    .setKey(((LabeledKey<?>) config.getKey()).getKey()).setValueType(config.getValueType())
                    .setDefaultValue(config.getDefaultValue()).addValueConverters(config.getValueConverters())
                    .setValueFilter(config.getValueFilter()).build();
            propertyLabels = ((LabeledKey<?>) config.getKey()).getLabels().getLabels();
        }

        return getPropertyValue(propertyConfig, propertyLabels);
    }

}
