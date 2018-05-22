package org.mydotey.scf.source.cascaded;

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
import org.mydotey.scf.facade.MoreSources;
import org.mydotey.scf.source.stringproperties.systemproperties.SystemPropertiesConfigurationSource;
import org.mydotey.scf.threading.SimpleTaskExecutor;
import org.mydotey.scf.threading.TaskExecutor;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public class CascadedConfigurationSourceTest {

    protected SystemPropertiesConfigurationSource createSource() {
        ConfigurationSourceConfig sourceConfig = MoreSources.newConfigBuilder().setName("system-properties")
                .setPriority(1).build();
        System.out.println("source config: " + sourceConfig + "\n");
        return MoreSources.newSystemPropertiesSource(sourceConfig);
    }

    protected ConfigurationManager createManager(long delayMs, long intervalMs,
            SystemPropertiesConfigurationSource source) {
        CascadedConfigurationSourceConfig sourceConfig = MoreSources.newCascadedSourceConfigBuilder()
                .setName("cascaded-system-properties").setPriority(1).setKeySeparator(".")
                .setCascadedFactors(Lists.newArrayList("part1", "part2")).build();
        CascadedConfigurationSource cascadedSource = MoreSources.newCascadedSource(sourceConfig, source);
        TaskExecutor taskExecutor = new SimpleTaskExecutor(1,
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("test-%d").build()) {

            @Override
            protected long getDelayMs() {
                return delayMs;
            }

            @Override
            protected long getIntervalMs() {
                return intervalMs;
            }

        };
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .setSources(Lists.newArrayList(cascadedSource)).setTaskExecutor(taskExecutor).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
    }

    @After
    public void tearDown() {
        System.clearProperty("exist");
        System.clearProperty("exist.part1");
        System.clearProperty("exist.part1.part2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDynamicWithoutTaskExecutor() {
        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .setSources(Lists.newArrayList(source)).build();
        System.out.println("manager config: " + managerConfig + "\n");
        ConfigurationManagers.newManager(managerConfig);
    }

    @Test
    public void testGetProperties() throws InterruptedException {
        System.setProperty("exist", "ok");

        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(5, 5, source);
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

        System.setProperty("exist", "ok2");
        Thread.sleep(20);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok2", property.getValue());
    }

    @Test
    public void testDynamicProperty() throws InterruptedException {
        System.setProperty("exist", "ok");

        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(1 * 1000, 1 * 1000, source);
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setDefaultValue("default").build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        System.setProperty("exist", "ok2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        source.setPropertyValue("exist", "ok3");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok3", property.getValue());

        System.setProperty("exist", "ok4");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok3", property.getValue());
        Thread.sleep(1 * 1000);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok4", property.getValue());
    }

    @Test
    public void testCascadedProperty() throws InterruptedException {
        System.setProperty("exist", "ok");

        SystemPropertiesConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(5, 5, source);
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setDefaultValue("default").build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        System.setProperty("exist.part1", "ok1");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok1", property.getValue());

        source.setPropertyValue("exist.part1.part2", "ok2");
        Thread.sleep(2);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok2", property.getValue());

        System.clearProperty("exist");
        source.clearProperty("exist.part1.part2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok1", property.getValue());
    }

}
