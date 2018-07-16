package org.mydotey.scf.labeled;

import java.util.Collection;
import java.util.Objects;

import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.DefaultConfigurationManager;
import org.mydotey.scf.PropertyConfig;
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

        PropertyConfig<?, V> noLabelPropertyConfig = AbstractLabeledConfigurationSource
                .removeLabels((PropertyConfig) propertyConfig);
        for (PropertyLabels propertyLabels = ((LabeledKey) propertyConfig.getKey())
                .getLabels(); propertyLabels != null; propertyLabels = propertyLabels.getAlternative()) {

            for (ConfigurationSource source : getSortedSources().values()) {
                if (!(source instanceof LabeledConfigurationSource) && !propertyLabels.getLabels().isEmpty())
                    continue;

                V value = null;
                if (propertyLabels.getLabels().isEmpty())
                    value = getPropertyValue(source, noLabelPropertyConfig);
                else
                    value = getPropertyValue((LabeledConfigurationSource) source, noLabelPropertyConfig,
                            propertyLabels.getLabels());

                value = applyValueFilter(propertyConfig, value);

                if (value != null)
                    return value;
            }
        }

        return propertyConfig.getDefaultValue();
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

}
