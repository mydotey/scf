package org.mydotey.scf;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.mydotey.scf.facade.ConfigurationSources;

import com.google.common.collect.Lists;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationManagerTest {

    protected TestConfigurationSource createSource(int priority) {
        ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfigBuilder().setName("test-source")
                .setPriority(priority).build();
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

    protected TestDynamicConfigurationSource createDynamicSource(int priority) {
        ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfigBuilder().setName("test-source")
                .setPriority(priority).build();
        HashMap<String, String> properties = new HashMap<>();
        properties.put("exist", "ok.2");
        properties.put("exist2", "ok2.2");
        properties.put("exist3", "ok3.2");
        properties.put("exist4", "ok4.2");
        TestDynamicConfigurationSource source = new TestDynamicConfigurationSource(sourceConfig, properties);
        System.out.println("source config: " + sourceConfig + "\n");
        return source;
    }

    protected ConfigurationManager createManager(ConfigurationSource... sources) {
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSources(Lists.newArrayList(sources)).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
    }

    @Test
    public void testGetProperties() {
        ConfigurationManager manager = createManager(createSource(1));
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
        ConfigurationManager manager = createManager(createSource(1));
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
        ConfigurationManager manager = createManager(createSource(1));
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
        ConfigurationManager manager = createManager(createSource(1));
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

    @Test
    public void testGetPropertyWithDynamicSource() {
        TestDynamicConfigurationSource source = createDynamicSource(1);
        ConfigurationManager manager = createManager(source);
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
        TestConfigurationSource source1 = createSource(1);
        TestDynamicConfigurationSource source2 = createDynamicSource(2);
        ConfigurationManager manager = createManager(source1, source2);
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

}
