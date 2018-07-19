package org.mydotey.scf;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.mydotey.scf.facade.ConfigurationSources;

import com.google.common.collect.ImmutableMap;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationManagerTest {

    protected TestConfigurationSource createSource() {
        ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfig("test-source");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("exist", "ok");
        properties.put("exist2", "ok2");
        properties.put("exist3", "ok3");
        properties.put("exist4", "ok4");
        properties.put("exist5", "ok5");
        TestConfigurationSource source = new TestConfigurationSource(sourceConfig, properties);
        System.out.println("source config: " + sourceConfig + "\n");
        return source;
    }

    protected TestDynamicConfigurationSource createDynamicSource() {
        ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfig("test-source");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("exist", "ok.2");
        properties.put("exist2", "ok2.2");
        properties.put("exist3", "ok3.2");
        properties.put("exist4", "ok4.2");
        TestDynamicConfigurationSource source = new TestDynamicConfigurationSource(sourceConfig, properties);
        System.out.println("source config: " + sourceConfig + "\n");
        return source;
    }

    protected ConfigurationManager createManager(Map<Integer, ConfigurationSource> sources) {
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSources(sources).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatePrioritySource() {
        TestConfigurationSource source1 = createSource();
        TestDynamicConfigurationSource source2 = createDynamicSource();
        ConfigurationManagers.newConfigBuilder().addSource(1, source1).addSource(1, source2);
    }

    @Test
    public void testGetProperties() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("not-exist2")
                .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("default", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("exist")
                .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSameKeyDifferentConfig() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("not-exist")
                .setValueType(String.class).setDefaultValue("default").build();
        manager.getProperty(propertyConfig);
    }

    @Test
    public void testSameConfigSameProperty() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        Property<String, String> property2 = manager.getProperty(propertyConfig);
        System.out.println("property2: " + property + "\n");
        Assert.assertTrue(property == property2);

        PropertyConfig<String, String> propertyConfig2 = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property3 = manager.getProperty(propertyConfig2);
        System.out.println("property3: " + property2 + "\n");
        Assert.assertTrue(property == property3);
    }

    @Test
    public void testGetPropertyWithFilter() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setValueFilter(v -> {
                    if (Objects.equals("ok", v))
                        return "ok_new";
                    return null;
                }).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok_new", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("exist2")
                .setValueType(String.class).setValueFilter(v -> {
                    return v.length() >= 8 && v.length() <= 32 ? v : null;
                }).build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertNull(property.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyWithDiffFilterInSimilarConfig() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setValueFilter(v -> {
                    if (Objects.equals("ok", v))
                        return "ok_new";
                    return null;
                }).build();
        System.out.println("propertyConfig: " + propertyConfig + "\n");
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals("ok_new", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("exist")
                .setValueType(String.class).setValueFilter(v -> {
                    if (Objects.equals("ok", v))
                        return "ok_new";
                    return null;
                }).build();
        System.out.println("propertyConfig: " + propertyConfig + "\n");
        manager.getProperty(propertyConfig);
    }

    @Test
    public void testGetPropertyWithDynamicSource() {
        TestDynamicConfigurationSource source = createDynamicSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok.2", property.getValue());

        source.setPropertyValue("exist", "okx");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("okx", property.getValue());

        source.setPropertyValue("exist", "ok.2");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok.2", property.getValue());

        AtomicBoolean touched = new AtomicBoolean();
        property.addChangeListener(p -> touched.set(true));
        source.setPropertyValue("exist", "okx");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("okx", property.getValue());
        Assert.assertTrue(touched.get());
    }

    @Test
    public void testGetPropertiesMultipleSource() {
        TestConfigurationSource source1 = createSource();
        TestDynamicConfigurationSource source2 = createDynamicSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source1, 2, source2));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("not-exist2")
                .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("default", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("exist")
                .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok.2", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder().setKey("exist5")
                .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok5", property.getValue());

        source2.setPropertyValue("exist5", "ok5.2");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok5.2", property.getValue());

        source2.setPropertyValue("exist5", null);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok5", property.getValue());
    }

    @Test
    public void testChangeListener() {
        TestDynamicConfigurationSource source = createDynamicSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source));
        AtomicInteger changeCount = new AtomicInteger();
        manager.addChangeListener(e -> {
            changeCount.incrementAndGet();
            System.out.println("property changed: " + e);
        });

        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        AtomicInteger changeCount2 = new AtomicInteger();
        property.addChangeListener(p -> changeCount2.incrementAndGet());

        source.setPropertyValue("exist", "okx");
        Assert.assertEquals(1, changeCount.get());
        Assert.assertEquals(1, changeCount2.get());

        source.setPropertyValue("exist", "ok.2");
        Assert.assertEquals(2, changeCount.get());
        Assert.assertEquals(2, changeCount2.get());

        source.setPropertyValue("exist", "okx");
        Assert.assertEquals(3, changeCount.get());
        Assert.assertEquals(3, changeCount2.get());

        // value not change, no change event
        source.setPropertyValue("exist", "okx");
        Assert.assertEquals(3, changeCount.get());
        Assert.assertEquals(3, changeCount2.get());
    }

}
