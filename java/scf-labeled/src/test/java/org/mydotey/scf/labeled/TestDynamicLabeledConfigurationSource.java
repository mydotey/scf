package org.mydotey.scf.labeled;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.mydotey.scf.ConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class TestDynamicLabeledConfigurationSource extends TestLabeledConfigurationSource {

    public TestDynamicLabeledConfigurationSource(ConfigurationSourceConfig config,
            Collection<TestDataCenterSetting> dataCenterSettings) {
        super(config, dataCenterSettings);
    }

    @Override
    protected void init() {
        _settings = new ConcurrentHashMap<>();
    }

    public void updateSetting(TestDataCenterSetting setting) {
        Objects.requireNonNull(setting, "setting is null");
        Objects.requireNonNull(setting.getKey(), "setting.key is null");

        TestDataCenterSetting oldValue = _settings.get(setting);
        if (oldValue != null) {
            if (Objects.equals(oldValue.getValue(), setting.getValue()))
                return;
        }

        setting = setting.clone();
        _settings.put(setting, setting);

        raiseChangeEvent();
    }

    public void removeSetting(TestDataCenterSetting setting) {
        Objects.requireNonNull(setting, "setting is null");
        Objects.requireNonNull(setting.getKey(), "setting.key is null");

        TestDataCenterSetting oldValue = _settings.remove(setting);

        if (oldValue == null)
            return;

        raiseChangeEvent();
    }

}
