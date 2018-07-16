package org.mydotey.scf.source.stringproperty.memorymap;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.ConfigurationProperties;
import org.mydotey.scf.facade.StringPropertySources;
import org.mydotey.scf.threading.TaskExecutor;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public class MemoryMapConfigurationSourceTest {

    protected MemoryMapConfigurationSource createSource() {
        MemoryMapConfigurationSource source = StringPropertySources.newMemoryMapSource("memory-map");
        source.setPropertyValue("exist", "ok");
        return source;
    }

    protected ConfigurationManager createManager(MemoryMapConfigurationSource source) {
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                .addSource(1, source).setTaskExecutor(new TaskExecutor(1)).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
    }

    @Test
    public void testGetProperties() throws InterruptedException {
        MemoryMapConfigurationSource source = createSource();
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

        source.setPropertyValue("exist", "ok2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok2", property.getValue());
    }

    @Test
    public void testDynamicProperty() throws InterruptedException {
        MemoryMapConfigurationSource source = createSource();
        ConfigurationManager manager = createManager(source);
        PropertyConfig<String, String> propertyConfig = ConfigurationProperties.<String, String> newConfigBuilder()
                .setKey("exist").setValueType(String.class).setDefaultValue("default").build();
        Property<String, String> property = manager.getProperty(propertyConfig);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        source.setPropertyValue("exist", "ok2");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok2", property.getValue());

        source.setPropertyValue("exist", "ok");
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());

        source.setPropertyValue("exist", null);
        Thread.sleep(10);
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("default", property.getValue());
    }

}
