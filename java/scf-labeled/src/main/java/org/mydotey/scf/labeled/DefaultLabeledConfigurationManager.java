package org.mydotey.scf.labeled;

import java.util.Collection;
import java.util.Objects;

import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.DefaultConfigurationManager;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author koqizhao
 *
 * Jun 15, 2018
 */
public class DefaultLabeledConfigurationManager extends DefaultConfigurationManager
        implements LabeledConfigurationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigurationManager.class);

    public DefaultLabeledConfigurationManager(ConfigurationManagerConfig config) {
        super(config);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig) {
        Objects.requireNonNull(propertyConfig, "propertyConfig is null");

        if (!(propertyConfig.getKey() instanceof LabeledKey))
            return super.getPropertyValue(propertyConfig);

        PropertyConfig<?, V> rawPropertyConfig = toRawPropertyConfig((PropertyConfig) propertyConfig);
        V value = null;
        for (PropertyLabels propertyLabels = ((LabeledKey) propertyConfig.getKey())
                .getLabels(); propertyLabels != null; propertyLabels = propertyLabels.getAlternative()) {

            for (ConfigurationSource source : getSortedSources()) {
                if (!(source instanceof LabeledConfigurationSource))
                    continue;

                value = getPropertyValue((LabeledConfigurationSource) source, rawPropertyConfig,
                        propertyLabels.getLabels());

                value = applyValueFilter(propertyConfig, value);

                if (value != null)
                    break;
            }

            if (value != null)
                break;
        }

        if (value == null)
            return super.getPropertyValue(rawPropertyConfig);

        return value;
    }

    protected <K, V> V getPropertyValue(LabeledConfigurationSource source, PropertyConfig<K, V> propertyConfig,
            Collection<PropertyLabel> labels) {
        V value = null;
        try {
            value = source.getPropertyValue(propertyConfig, labels);
        } catch (Exception e) {
            String message = String.format(
                    "error occurred when getting property value, ignore the source. source: %s, propertyConfig: %s",
                    source, propertyConfig);
            LOGGER.error(message, e);
        }

        return value;
    }

    protected <K, V> PropertyConfig<K, V> toRawPropertyConfig(PropertyConfig<LabeledKey<K>, V> config) {
        return ConfigurationProperties.<K, V> newConfigBuilder().setKey(config.getKey().getKey())
                .setValueType(config.getValueType()).setDefaultValue(config.getDefaultValue())
                .addValueConverters(config.getValueConverters()).setValueFilter(config.getValueFilter()).build();
    }

}
