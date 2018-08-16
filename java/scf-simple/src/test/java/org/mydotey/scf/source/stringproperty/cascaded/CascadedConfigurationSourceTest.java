package org.mydotey.scf.source.stringproperty.cascaded;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.mydotey.scf.facade.StringPropertySources;
import org.mydotey.scf.source.stringproperty.systemproperties.SystemPropertiesConfigurationSource;
import org.mydotey.scf.threading.TaskExecutor;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public class CascadedConfigurationSourceTest {

    protected SystemPropertiesConfigurationSource createSource() {
        return StringPropertySources.newSystemPropertiesSource("system-properties");
    }

    protected ConfigurationManager createManager(SystemPropertiesConfigurationSource source) {
        CascadedConfigurationSourceConfig<ConfigurationSourceConfig> sourceConfig = StringPropertySources
                .newCascadedSourceConfigBuilder().setName("cascaded-system-properties").setKeySeparator(".")
                .addCascadedFactor("part1").addCascadedFactor("part2").setSource(source).build();
        CascadedConfigurationSource<ConfigurationSourceConfig> cascadedSource = StringPropertySources
                .newCascadedSource(sourceConfig);
        TaskExecutor taskExecutor = new TaskExecutor(1);
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSource(1, cascadedSource).setTaskExecutor(taskExecutor).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
    }

    protected ConfigurationManager createKeyCachedManager(SystemPropertiesConfigurationSource source) {
        CascadedConfigurationSourceConfig<ConfigurationSourceConfig> sourceConfig = StringPropertySources
                .newCascadedSourceConfigBuilder().setName("cascaded-system-properties").setKeySeparator(".")
                .addCascadedFactor("part1").addCascadedFactor("part2").setSource(source).build();
        CascadedConfigurationSource<ConfigurationSourceConfig> cascadedSource = new KeyCachedCascadedConfigurationSource<>(
                sourceConfig);
        TaskExecutor taskExecutor = new TaskExecutor(1);
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSource(1, cascadedSource).setTaskExecutor(taskExecutor).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
    }

    @After
    public void tearDown() {
        System.clearProperty("exist");
        System.clearProperty("exist.part1");
        System.clearProperty("exist.part1.part2");
    }

    @Test
    public void testGetProperties() throws InterruptedException {
        System.setProperty("exist", "ok");

        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(source);
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

        source.setProperty("exist", "ok2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok2", property.getValue());
    }

    @Test
    public void testDynamicProperty() throws InterruptedException {
        System.setProperty("exist", "ok");

        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(source);
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setDefaultValue("default").build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        System.setProperty("exist", "ok2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        source.setProperty("exist", "ok3");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok3", property.getValue());

        System.setProperty("exist", "ok4");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok3", property.getValue());
    }

    @Test
    public void testCascadedProperty() throws InterruptedException {
        System.setProperty("exist", "ok");

        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(source);
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setDefaultValue("default").build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        System.setProperty("exist.part1", "ok1");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        source.setProperty("exist.part1.part2", "ok2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok2", property.getValue());

        System.clearProperty("exist");
        source.clearProperty("exist.part1.part2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok1", property.getValue());
    }

    @Test
    public void testKeyCachedCascadedProperty() throws InterruptedException {
        System.setProperty("exist", "ok");

        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManager manager = createKeyCachedManager(source);
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setDefaultValue("default").build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        System.setProperty("exist.part1", "ok1");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        source.setProperty("exist.part1.part2", "ok2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok2", property.getValue());

        System.clearProperty("exist");
        source.clearProperty("exist.part1.part2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok1", property.getValue());
    }

}
