package org.mydotey.scf.labeled;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationManagerTest;
import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.mydotey.scf.facade.ConfigurationSources;
import org.mydotey.scf.facade.LabeledManagers;
import org.mydotey.scf.facade.LabeledProperties;

import com.google.common.collect.Lists;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class LabeledConfigurationManagerTest extends ConfigurationManagerTest {

    @Override
    protected ConfigurationManager createManager(ConfigurationSource... sources) {
        List<ConfigurationSource> sourceList = Lists.newArrayList(sources);
        ConfigurationSourceConfig config = ConfigurationSources.newConfigBuilder().setName("labeled-source")
                .setPriority(Integer.MAX_VALUE - 1).build();
        TestLabeledConfigurationSource source = createLabeledSource(config);
        sourceList.add(source);
        config = ConfigurationSources.newConfigBuilder().setName("dynamic-labeled-source")
                .setPriority(Integer.MAX_VALUE).build();
        source = createDynamicLabeledSource(config);
        sourceList.add(source);

        return createLabeledManager(sourceList);
    }

    protected ConfigurationManager createLabeledManager(List<ConfigurationSource> sources) {
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSources(sources).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return LabeledManagers.newManager(managerConfig);
    }

    protected TestLabeledConfigurationSource createLabeledSource(ConfigurationSourceConfig config) {
        TestDataCenterSetting setting = new TestDataCenterSetting("labeled-key-1", "v-0", null, null);
        TestDataCenterSetting setting1 = new TestDataCenterSetting("labeled-key-1", "v-1", "sh-1", "app-1");
        TestDataCenterSetting setting2 = new TestDataCenterSetting("labeled-key-1", "v-2", "sh-2", "app-1");
        TestDataCenterSetting setting3 = new TestDataCenterSetting("labeled-key-1", "v-3", "sh-1", "app-2");
        return new TestLabeledConfigurationSource(config, Lists.newArrayList(setting, setting1, setting2, setting3));
    }

    protected TestDynamicLabeledConfigurationSource createDynamicLabeledSource(ConfigurationSourceConfig config) {
        TestDataCenterSetting setting = new TestDataCenterSetting("labeled-key-1", "v-0-2", null, null);
        TestDataCenterSetting setting1 = new TestDataCenterSetting("labeled-key-1", "v-1-2", "sh-1", "app-1");
        TestDataCenterSetting setting2 = new TestDataCenterSetting("labeled-key-1", "v-2-2", "sh-2", "app-1");
        TestDataCenterSetting setting3 = new TestDataCenterSetting("labeled-key-1", "v-3-2", "sh-1", "app-2");
        return new TestDynamicLabeledConfigurationSource(config,
                Lists.newArrayList(setting, setting1, setting2, setting3));
    }

    @Test
    public void testGetLabeledProperty() throws InterruptedException {
        ConfigurationSourceConfig config = ConfigurationSources.newConfigBuilder().setName("labeled-source")
                .setPriority(1).build();
        TestLabeledConfigurationSource labeledSource = createLabeledSource(config);
        config = ConfigurationSources.newConfigBuilder().setName("dynamic-labeled-source").setPriority(2).build();
        TestDynamicLabeledConfigurationSource dynamicLabeledSource = createDynamicLabeledSource(config);
        ConfigurationManager manager = createLabeledManager(Lists.newArrayList(labeledSource, dynamicLabeledSource));

        List<PropertyLabel> labels = new ArrayList<>();
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1"));
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        PropertyLabels propertyLabels = LabeledProperties.newLabels(labels);
        LabeledKey<String> key = LabeledProperties.<String> newKeyBuilder().setKey("labeled-key-1")
                .setPropertyLabels(propertyLabels).build();
        PropertyConfig<LabeledKey<String>, String> propertyConfig = ConfigurationProperties
                .<LabeledKey<String>, String> newConfigBuilder().setKey(key).setValueType(String.class)
                .setDefaultValue("default-value-1").build();
        Property<LabeledKey<String>, String> property = manager.getProperty(propertyConfig);
        System.out.println(property);
        Assert.assertEquals("v-1-2", property.getValue());

        labels = new ArrayList<>();
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1-not-exist"));
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        propertyLabels = LabeledProperties.newLabels(labels);
        key = LabeledProperties.<String> newKeyBuilder().setKey("labeled-key-1").setPropertyLabels(propertyLabels)
                .build();
        propertyConfig = ConfigurationProperties.<LabeledKey<String>, String> newConfigBuilder().setKey(key)
                .setValueType(String.class).setDefaultValue("default-value-1").build();
        property = manager.getProperty(propertyConfig);
        System.out.println(property);
        Assert.assertEquals("default-value-1", property.getValue());

        labels = new ArrayList<>();
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1"));
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        propertyLabels = LabeledProperties.newLabels(labels);
        labels = new ArrayList<>();
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1-not-exist"));
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        propertyLabels = LabeledProperties.newLabels(labels, propertyLabels);
        key = LabeledProperties.<String> newKeyBuilder().setKey("labeled-key-1").setPropertyLabels(propertyLabels)
                .build();
        propertyConfig = ConfigurationProperties.<LabeledKey<String>, String> newConfigBuilder().setKey(key)
                .setValueType(String.class).setDefaultValue("default-value-1").build();
        property = manager.getProperty(propertyConfig);
        System.out.println(property);
        Assert.assertEquals("v-1-2", property.getValue());

        TestDataCenterSetting setting = new TestDataCenterSetting("labeled-key-1", "v-4-2", "sh-1-not-exist", "app-1");
        dynamicLabeledSource.updateSetting(setting);
        Thread.sleep(10);
        System.out.println(property);
        Assert.assertEquals("v-4-2", property.getValue());

        dynamicLabeledSource.removeSetting(setting);
        Thread.sleep(10);
        System.out.println(property);
        Assert.assertEquals("v-1-2", property.getValue());

        labels = new ArrayList<>();
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1-not-exist"));
        labels.add(LabeledProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        propertyLabels = LabeledProperties.newLabels(labels, PropertyLabels.EMPTY);
        key = LabeledProperties.<String> newKeyBuilder().setKey("labeled-key-1").setPropertyLabels(propertyLabels)
                .build();
        propertyConfig = ConfigurationProperties.<LabeledKey<String>, String> newConfigBuilder().setKey(key)
                .setValueType(String.class).setDefaultValue("default-value-1").build();
        property = manager.getProperty(propertyConfig);
        System.out.println(property);
        Assert.assertEquals("v-0-2", property.getValue());
    }
}
