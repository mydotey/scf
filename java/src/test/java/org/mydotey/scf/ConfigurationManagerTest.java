package org.mydotey.scf;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.mydotey.scf.facade.ConfigurationSources;
import org.mydotey.scf.type.AbstractTypeConverter;
import org.mydotey.scf.type.TypeConverter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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
        properties.put("exist_int", "1");
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

    protected TypeConverter<String, Integer> newTypeConverter() {
        return new AbstractTypeConverter<String, Integer>(String.class, Integer.class) {
            @Override
            public Integer convert(String source) {
                try {
                    return Integer.valueOf(source);
                } catch (Exception ex) {
                    return null;
                }
            }
        };
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
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("not-exist2")
            .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("default", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("exist")
            .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        PropertyConfig<String, Integer> propertyConfig2 = ConfigurationProperties.<String, Integer>newConfigBuilder()
            .setKey("exist2").setValueType(Integer.class).build();
        Property<String, Integer> property2 = manager.getProperty(propertyConfig2);
        System.out.println("property: " + property2 + "\n");
        Assert.assertEquals(null, property2.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSameKeyDifferentConfig() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("not-exist")
            .setValueType(String.class).setDefaultValue("default").build();
        manager.getProperty(propertyConfig);
    }

    @Test
    public void testSameConfigSameProperty() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        Property<String, String> property2 = manager.getProperty(propertyConfig);
        System.out.println("property2: " + property + "\n");
        Assert.assertTrue(property == property2);

        PropertyConfig<String, String> propertyConfig2 = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property3 = manager.getProperty(propertyConfig2);
        System.out.println("property3: " + property2 + "\n");
        Assert.assertTrue(property == property3);
    }

    @Test
    public void testGetPropertyWithConverter() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, Integer> propertyConfig = ConfigurationProperties.<String, Integer>newConfigBuilder()
            .setKey("exist_int").setValueType(Integer.class).addValueConverter(newTypeConverter()).build();
        Property<String, Integer> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(Integer.valueOf(1), property.getValue());

        propertyConfig = ConfigurationProperties.<String, Integer>newConfigBuilder()
            .setKey("exist").setValueType(Integer.class).addValueConverter(newTypeConverter()).build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertNull(property.getValue());

        propertyConfig = ConfigurationProperties.<String, Integer>newConfigBuilder()
            .setKey("not_exist").setValueType(Integer.class).addValueConverter(newTypeConverter()).build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertNull(property.getValue());
    }

    @Test
    public void testGetPropertyWithFilter() {
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("exist").setValueType(String.class).setValueFilter(v -> {
                if (Objects.equals("ok", v))
                    return "ok_new";
                return null;
            }).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok_new", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("exist2")
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
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("exist").setValueType(String.class).setValueFilter(v -> {
                if (Objects.equals("ok", v))
                    return "ok_new";
                return null;
            }).build();
        System.out.println("propertyConfig: " + propertyConfig + "\n");
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals("ok_new", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("exist")
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
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
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
        property.addChangeListener(e -> System.out.printf("property: %s, changeTime: %s, from: %s, to: %s\n",
            e.getProperty(), e.getChangeTime(), e.getOldValue(), e.getNewValue()));
        source.setPropertyValue("exist", "okx");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("okx", property.getValue());
        Assert.assertTrue(touched.get());
    }

    @Test
    public void testGetPropertyWithComparator() {
        TestDynamicConfigurationSource source = createDynamicSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source));
        Set<String> equalsSet = ImmutableSet.of("e.1", "e.2");
        Comparator<String> customComparator = (o1, o2) -> {
            if (equalsSet.contains(o1) && equalsSet.contains(o2))
                return 0;
            return o1 == o2 ? 0 : -1;
        };
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("exist").setValueType(String.class).setValueComparator(customComparator).build();
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
        property.addChangeListener(e -> System.out.printf("property: %s, changeTime: %s, from: %s, to: %s\n",
            e.getProperty(), e.getChangeTime(), e.getOldValue(), e.getNewValue()));

        source.setPropertyValue("exist", "okx");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("okx", property.getValue());
        Assert.assertTrue(touched.get());

        source.setPropertyValue("exist", "e.1");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("e.1", property.getValue());

        source.setPropertyValue("exist", "e.2");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("e.1", property.getValue());

        source.setPropertyValue("exist", "n.1");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("n.1", property.getValue());
    }

    @Test
    public void testGetPropertiesMultipleSource() {
        TestConfigurationSource source1 = createSource();
        TestDynamicConfigurationSource source2 = createDynamicSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source1, 2, source2));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey("not-exist").setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("not-exist2")
            .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("default", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("exist")
            .setValueType(String.class).setDefaultValue("default").build();
        property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok.2", property.getValue());

        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey("exist5")
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

        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
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

    @Test
    public void testPropertyConfigDoc() {
        String doc = null;
        String key = "not-exist";
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey(key).setValueType(String.class).build();
        Assert.assertEquals(doc, propertyConfig.getDoc());
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals(doc, property.getConfig().getDoc());

        doc = "test-doc";
        key = "not-exist-2";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).setDoc(doc).build();
        Assert.assertEquals(doc, propertyConfig.getDoc());
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(doc, property.getConfig().getDoc());
    }

    @Test
    public void testPropertyConfigRequired() {
        boolean required = false;
        String key = "not-exist";
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey(key).setValueType(String.class).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals(required, property.getConfig().isRequired());
        String value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals(null, value);

        required = true;
        key = "not-exist-2";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).setRequired(required).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        try {
            property = manager.getProperty(propertyConfig);
            Assert.fail();
        } catch (IllegalStateException e) {

        }
        try {
            value = manager.getPropertyValue(propertyConfig);
            Assert.fail();
        } catch (IllegalStateException e) {

        }

        required = false;
        key = "exist";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).setRequired(required).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(required, property.getConfig().isRequired());
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals("ok", value);

        required = true;
        key = "exist2";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).setRequired(required).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(required, property.getConfig().isRequired());
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals("ok2", value);
    }

    @Test
    public void testPropertyConfigRequiredDefault() {
        boolean required = false;
        String key = "not-exist";
        String defaultValue = "default";
        ConfigurationManager manager = createManager(ImmutableMap.of(1, createSource()));
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey(key).setValueType(String.class).setDefaultValue(defaultValue).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals(required, property.getConfig().isRequired());
        String value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals(defaultValue, value);

        required = true;
        key = "not-exist-2";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).setRequired(required).setDefaultValue(defaultValue).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        property = manager.getProperty(propertyConfig);
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals(defaultValue, value);
    }

    @Test
    public void testPropertyConfigRequiredDynamic() {
        TestDynamicConfigurationSource source = createDynamicSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source));

        boolean required = false;
        String key = "exist";
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey(key).setValueType(String.class).setRequired(required).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals(required, property.getConfig().isRequired());
        Assert.assertEquals("ok.2", property.getValue());
        String value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals("ok.2", value);
        source.setPropertyValue(key, null);
        Assert.assertEquals(null, property.getValue());
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals(null, value);

        required = true;
        key = "exist2";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).setRequired(required).build();
        Assert.assertEquals(required, propertyConfig.isRequired());
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(required, property.getConfig().isRequired());
        Assert.assertEquals("ok2.2", property.getValue());
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals("ok2.2", value);
        source.setPropertyValue(key, null);
        Assert.assertEquals("ok2.2", property.getValue());
        try {
            value = manager.getPropertyValue(propertyConfig);
            Assert.fail();
        } catch (IllegalStateException e) {

        }
    }

    @Test
    public void testPropertyConfigStatic() {
        TestDynamicConfigurationSource source = createDynamicSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source));

        boolean isStatic = false;
        String key = "exist";
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey(key).setValueType(String.class).setStatic(isStatic).build();
        Assert.assertEquals(isStatic, propertyConfig.isStatic());
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals(isStatic, property.getConfig().isStatic());
        Assert.assertEquals("ok.2", property.getValue());
        String value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals("ok.2", value);
        source.setPropertyValue(key, null);
        Assert.assertEquals(null, property.getValue());
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals(null, value);

        isStatic = true;
        key = "exist2";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).setStatic(isStatic).build();
        Assert.assertEquals(isStatic, propertyConfig.isStatic());
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(isStatic, property.getConfig().isStatic());
        Assert.assertEquals("ok2.2", property.getValue());
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals("ok2.2", value);
        source.setPropertyValue(key, null);
        Assert.assertEquals("ok2.2", property.getValue());
        value = manager.getPropertyValue(propertyConfig);
        Assert.assertEquals(null, value);
    }

    @Test
    public void testPropertySource() {
        ConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(ImmutableMap.of(1, source));

        String key = "not-exist";
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder()
            .setKey(key).setValueType(String.class).build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        Assert.assertEquals(null, property.getSource());

        key = "exist";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).build();
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(source, property.getSource());

        TestDynamicConfigurationSource dynamicSource = createDynamicSource();
        manager = createManager(ImmutableMap.of(1, dynamicSource));

        key = "not-exist";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).build();
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(null, property.getSource());
        dynamicSource.setPropertyValue(key, "ok");
        Assert.assertEquals(dynamicSource, property.getSource());

        key = "exist";
        propertyConfig = ConfigurationProperties.<String, String>newConfigBuilder().setKey(key)
            .setValueType(String.class).build();
        property = manager.getProperty(propertyConfig);
        Assert.assertEquals(dynamicSource, property.getSource());
        dynamicSource.setPropertyValue(key, null);
        Assert.assertEquals(null, property.getSource());
    }
}
