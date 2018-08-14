using System;
using System.Threading;

using Xunit;
using MyDotey.SCF.Facade;
using MyDotey.SCF.Threading;

namespace MyDotey.SCF.Source.StringProperty.MemoryMap
{
    /**
     * @author koqizhao
     *
     * May 22, 2018
     */
    public class MemoryMapConfigurationSourceTest
    {
        protected MemoryMapConfigurationSource createSource()
        {
            MemoryMapConfigurationSource source = StringPropertySources.newMemoryMapSource("memory-map");
            source.setPropertyValue("exist", "ok");
            return source;
        }

        protected ConfigurationManager createManager(MemoryMapConfigurationSource source)
        {
            ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                    .addSource(1, source).setTaskExecutor(new TaskExecutor().run).build();
            Console.WriteLine("manager config: " + managerConfig + "\n");
            return ConfigurationManagers.newManager(managerConfig);
        }

        [Fact]
        public void testGetProperties()
        {
            MemoryMapConfigurationSource source = createSource();
            ConfigurationManager manager = createManager(source);
            PropertyConfig<String, String> propertyConfig = ConfigurationProperties.newConfigBuilder<String, String>()
                    .setKey("not-exist").build();
            Property<String, String> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<String, String>().setKey("not-exist2")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("default", property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<String, String>().setKey("exist")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.getValue());

            source.setPropertyValue("exist", "ok2");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok2", property.getValue());
        }

        [Fact]
        public void testDynamicProperty()
        {
            MemoryMapConfigurationSource source = createSource();
            ConfigurationManager manager = createManager(source);
            PropertyConfig<String, String> propertyConfig = ConfigurationProperties.newConfigBuilder<String, String>()
                    .setKey("exist").setDefaultValue("default").build();
            Property<String, String> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.getValue());

            source.setPropertyValue("exist", "ok2");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok2", property.getValue());

            source.setPropertyValue("exist", "ok");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.getValue());

            source.setPropertyValue("exist", null);
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("default", property.getValue());
        }
    }
}