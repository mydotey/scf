package org.mydotey.scf.labeled;

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

    @Override
    protected Object getPropertyValue(Object key, Collection<PropertyLabel> labels) {
        if (key.getClass() != String.class)
            return null;

        TestDataCenterSetting setting = new TestDataCenterSetting((String) key, null, null, null);
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
        return labeledSetting == null ? null : labeledSetting.getValue();
    }

}
