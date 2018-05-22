package org.mydotey.scf.source.stringproperties.systemproperties;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.mydotey.scf.facade.ConfigurationSources;

import com.google.common.collect.Lists;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public class SystemPropertiesConfigurationSourceTest {

    protected SystemPropertiesConfigurationSource createSource() {
        ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfigBuilder().setName("system-properties")
                .setPriority(1).build();
        System.out.println("source config: " + sourceConfig + "\n");
        return ConfigurationSources.newSystemPropertiesSource(sourceConfig);
    }

    protected ConfigurationManager createManager(long delayMs, long intervalMs,
            SystemPropertiesConfigurationSource source) {
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .setSources(Lists.newArrayList(source)).setTaskExecutor(new TestTaskExecutor(delayMs, intervalMs))
                .build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
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

}
