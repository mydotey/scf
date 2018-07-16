package org.mydotey.scf.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.labeled.LabeledConfigurationManager;
import org.mydotey.scf.labeled.LabeledKey;
import org.mydotey.scf.labeled.PropertyLabel;
import org.mydotey.scf.labeled.PropertyLabels;
import org.mydotey.scf.labeled.TestDataCenterSetting;
import org.mydotey.scf.labeled.TestLabeledConfigurationSource;
import org.mydotey.scf.source.stringproperty.propertiesfile.PropertiesFileConfigurationSourceConfig;
import org.mydotey.scf.type.string.StringToIntConverter;
import org.mydotey.scf.type.string.StringToLongConverter;

import com.google.common.collect.Lists;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class LabeledStringPropertiesTest extends StringPropertiesTest {

    @Override
    protected LabeledConfigurationManager createManager(String fileName) {
        PropertiesFileConfigurationSourceConfig sourceConfig = StringPropertySources
                .newPropertiesFileSourceConfigBuilder().setName("properties-source").setFileName(fileName).build();
        System.out.println("source config: " + sourceConfig + "\n");
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSource(1, StringPropertySources.newPropertiesFileSource(sourceConfig)).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return LabeledConfigurationManagers.newManager(managerConfig);
    }

    @Override
    protected StringProperties createStringProperties(String fileName) {
        LabeledConfigurationManager manager = createManager(fileName);
        return new StringProperties(manager);
    }

    protected LabeledStringProperties createLabeledStringProperties() {
        LabeledConfigurationManager manager = createLabeledManager();
        return new LabeledStringProperties(manager);
    }

    protected LabeledConfigurationManager createLabeledManager() {
        ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfig("labeled-source");
        System.out.println("source config: " + sourceConfig + "\n");
        TestDataCenterSetting setting1 = new TestDataCenterSetting("exist", "ok", "sh-1", "app-1");
        TestDataCenterSetting setting2 = new TestDataCenterSetting("int-value", "1", "sh-1", "app-1");
        TestDataCenterSetting setting3 = new TestDataCenterSetting("list-value", "s1, s2, s3", "sh-1", "app-1");
        TestDataCenterSetting setting4 = new TestDataCenterSetting("map-value", "k1: v1, k2: v2, k3: v3", "sh-1",
                "app-1");
        TestDataCenterSetting setting5 = new TestDataCenterSetting("int-list-value", "1, 2, 3", "sh-1", "app-1");
        TestDataCenterSetting setting6 = new TestDataCenterSetting("int-long-map-value", "1: 2, 3: 4, 5: 6", "sh-1",
                "app-1");
        TestLabeledConfigurationSource source = new TestLabeledConfigurationSource(sourceConfig,
                Lists.newArrayList(setting1, setting2, setting3, setting4, setting5, setting6));
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSource(1, source).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return LabeledConfigurationManagers.newManager(managerConfig);
    }

    @Test
    public void testGetLabeledProperties() {
        LabeledStringProperties labeledStringProperties = createLabeledStringProperties();

        List<PropertyLabel> labels = new ArrayList<>();
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1"));
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        PropertyLabels propertyLabels = LabeledConfigurationProperties.newLabels(labels);
        LabeledKey<String> key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("not-exist")
                .setPropertyLabels(propertyLabels).build();

        Property<LabeledKey<String>, String> property = labeledStringProperties.getStringProperty(key);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("not-exist2")
                .setPropertyLabels(propertyLabels).build();
        property = labeledStringProperties.getStringProperty(key, "default");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("default", property.getValue());

        key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("exist").setPropertyLabels(propertyLabels)
                .build();
        property = labeledStringProperties.getStringProperty(key, "default");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());
    }

    @Test
    public void testGetTypedLabeledProperties() {
        LabeledStringProperties labeledStringProperties = createLabeledStringProperties();

        List<PropertyLabel> labels = new ArrayList<>();
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1"));
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        PropertyLabels propertyLabels = LabeledConfigurationProperties.newLabels(labels);
        LabeledKey<String> key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("int-value")
                .setPropertyLabels(propertyLabels).build();

        Property<LabeledKey<String>, Integer> property = labeledStringProperties.getIntProperty(key);
        System.out.println("property: " + property + "\n");
        Integer expected = Integer.valueOf(1);
        Assert.assertEquals(expected, property.getValue());

        key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("list-value")
                .setPropertyLabels(propertyLabels).build();
        Property<LabeledKey<String>, List<String>> property2 = labeledStringProperties.getListProperty(key);
        System.out.println("property: " + property2 + "\n");
        List<String> expected2 = Lists.newArrayList("s1", "s2", "s3");
        Assert.assertEquals(expected2, property2.getValue());

        key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("map-value")
                .setPropertyLabels(propertyLabels).build();
        Property<LabeledKey<String>, Map<String, String>> property3 = labeledStringProperties.getMapProperty(key);
        System.out.println("property: " + property3 + "\n");
        Map<String, String> expected3 = new HashMap<>();
        expected3.put("k1", "v1");
        expected3.put("k2", "v2");
        expected3.put("k3", "v3");
        Assert.assertEquals(expected3, property3.getValue());

        key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("int-list-value")
                .setPropertyLabels(propertyLabels).build();
        Property<LabeledKey<String>, List<Integer>> property4 = labeledStringProperties.getListProperty(key,
                StringToIntConverter.DEFAULT);
        System.out.println("property: " + property4 + "\n");
        List<Integer> expected4 = Lists.newArrayList(1, 2, 3);
        Assert.assertEquals(expected4, property4.getValue());

        key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("int-long-map-value")
                .setPropertyLabels(propertyLabels).build();
        Property<LabeledKey<String>, Map<Integer, Long>> property5 = labeledStringProperties.getMapProperty(key,
                StringToIntConverter.DEFAULT, StringToLongConverter.DEFAULT);
        System.out.println("property: " + property5 + "\n");
        Map<Integer, Long> expected5 = new HashMap<>();
        expected5.put(1, 2L);
        expected5.put(3, 4L);
        expected5.put(5, 6L);
        Assert.assertEquals(expected5, property5.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSameKeyDifferentConfigForLabeledProperties() {
        LabeledStringProperties labeledStringProperties = createLabeledStringProperties();

        List<PropertyLabel> labels = new ArrayList<>();
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1"));
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        PropertyLabels propertyLabels = LabeledConfigurationProperties.newLabels(labels);
        LabeledKey<String> key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("map-value")
                .setPropertyLabels(propertyLabels).build();

        Property<LabeledKey<String>, Map<String, String>> property = labeledStringProperties.getMapProperty(key);
        Map<String, String> expected = new HashMap<>();
        expected.put("k1", "v1");
        expected.put("k2", "v2");
        expected.put("k3", "v3");
        Assert.assertEquals(expected, property.getValue());

        labeledStringProperties.getMapProperty(key, StringToIntConverter.DEFAULT, StringToLongConverter.DEFAULT);
    }

    @Test
    public void testSameConfigSameLabeledProperty() {
        LabeledStringProperties labeledStringProperties = createLabeledStringProperties();

        List<PropertyLabel> labels = new ArrayList<>();
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.DC_KEY, "sh-1"));
        labels.add(LabeledConfigurationProperties.newLabel(TestDataCenterSetting.APP_KEY, "app-1"));
        PropertyLabels propertyLabels = LabeledConfigurationProperties.newLabels(labels);
        LabeledKey<String> key = LabeledConfigurationProperties.<String> newKeyBuilder().setKey("map-value")
                .setPropertyLabels(propertyLabels).build();

        Property<LabeledKey<String>, Map<String, String>> property = labeledStringProperties.getMapProperty(key);
        Map<String, String> expected = new HashMap<>();
        expected.put("k1", "v1");
        expected.put("k2", "v2");
        expected.put("k3", "v3");
        Assert.assertEquals(expected, property.getValue());

        Property<LabeledKey<String>, Map<String, String>> property2 = labeledStringProperties.getMapProperty(key);
        System.out.println("property2: " + property + "\n");
        Assert.assertTrue(property == property2);
    }

}
