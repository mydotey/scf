package org.mydotey.scf.labeled;

import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.type.TypeConverter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.mydotey.scf.ConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class TestLabeledConfigurationSource extends AbstractLabeledConfigurationSource {

    protected Map<TestDataCenterSetting, TestDataCenterSetting> _settings;

    public TestLabeledConfigurationSource(ConfigurationSourceConfig config,
            Collection<TestDataCenterSetting> dataCenterSettings) {
        super(config);

        Objects.requireNonNull(dataCenterSettings, "dataCenterSettings is null");

        init();

        dataCenterSettings.forEach(s -> {
            if (s != null) {
                TestDataCenterSetting copy = s.clone();
                _settings.put(copy, copy);
            }
        });
    }

    protected void init() {
        _settings = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> V getPropertyValue(PropertyConfig<K, V> propertyConfig, Collection<PropertyLabel> labels) {
        if (propertyConfig.getKey().getClass() != String.class)
            return null;

        TestDataCenterSetting setting = new TestDataCenterSetting((String) propertyConfig.getKey(), null, null, null);
        if (labels != null) {
            labels.forEach(l -> {
                if (l == null)
                    return;

                if (Objects.equals(l.getKey(), TestDataCenterSetting.DC_KEY))
                    setting.setDc((String) l.getValue());

                if (Objects.equals(l.getKey(), TestDataCenterSetting.APP_KEY))
                    setting.setApp((String) l.getValue());
            });
        }

        TestDataCenterSetting labeledSetting = _settings.get(setting);

        if (labeledSetting == null)
            return null;

        String value = labeledSetting.getValue();
        if (value == null || value.isEmpty())
            return null;

        value = value.trim();
        if (value.isEmpty())
            return null;

        if (propertyConfig.getValueType() == String.class)
            return (V) value;

        for (TypeConverter<?, ?> typeConverter : propertyConfig.getValueConverters()) {
            if (typeConverter.getSourceType() == String.class
                    && propertyConfig.getValueType().isAssignableFrom(typeConverter.getTargetType()))
                return ((TypeConverter<String, V>) typeConverter).convert(value);
        }

        return null;
    }

}
