using System;
using System.Threading;

using Xunit;
using MyDotey.SCF.Facade;
using MyDotey.SCF.Threading;
using MyDotey.SCF.Source.StringProperty.MemoryMap;

namespace MyDotey.SCF.Source.StringProperty.Cascaded
{
    /**
     * @author koqizhao
     *
     * May 22, 2018
     */
    public class CascadedConfigurationSourceTest
    {
        protected MemoryMapConfigurationSource createSource()
        {
            return StringPropertySources.newMemoryMapSource("memory-map");
        }

        protected ConfigurationManager createManager(MemoryMapConfigurationSource source)
        {
            CascadedConfigurationSourceConfig<ConfigurationSourceConfig> sourceConfig = StringPropertySources.newCascadedSourceConfigBuilder<ConfigurationSourceConfig>()
                    .setName("cascaded-memory-map").setKeySeparator(".").addCascadedFactor("part1")
                    .addCascadedFactor("part2").setSource(source).build();
            CascadedConfigurationSource<ConfigurationSourceConfig> cascadedSource = StringPropertySources.newCascadedSource<ConfigurationSourceConfig>(sourceConfig);
            TaskExecutor taskExecutor = new TaskExecutor();
            ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                    .addSource(1, cascadedSource).setTaskExecutor(taskExecutor.run).build();
            Console.WriteLine("manager config: " + managerConfig + "\n");
            return ConfigurationManagers.newManager(managerConfig);
        }

        protected ConfigurationManager createKeyCachedManager(MemoryMapConfigurationSource source)
        {
            CascadedConfigurationSourceConfig<ConfigurationSourceConfig> sourceConfig = StringPropertySources.newCascadedSourceConfigBuilder<ConfigurationSourceConfig>()
                    .setName("cascaded-memory-map").setKeySeparator(".").addCascadedFactor("part1")
                    .addCascadedFactor("part2").setSource(source).build();
            CascadedConfigurationSource<ConfigurationSourceConfig> cascadedSource = new KeyCachedCascadedConfigurationSource<ConfigurationSourceConfig>(sourceConfig);
            TaskExecutor taskExecutor = new TaskExecutor();
            ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                    .addSource(1, cascadedSource).setTaskExecutor(taskExecutor.run).build();
            Console.WriteLine("manager config: " + managerConfig + "\n");
            return ConfigurationManagers.newManager(managerConfig);
        }

        [Fact]
        public void testGetProperties()
        {
            MemoryMapConfigurationSource source = createSource();
            source.setPropertyValue("exist", "ok");

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
            source.setPropertyValue("exist", "ok");

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

            source.setPropertyValue("exist", "ok3");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok3", property.getValue());

            source.setPropertyValue("exist", "ok4");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok4", property.getValue());
        }

        [Fact]
        public void testCascadedProperty()
        {
            MemoryMapConfigurationSource source = createSource();
            source.setPropertyValue("exist", "ok");

            ConfigurationManager manager = createManager(source);

            PropertyConfig<String, String> propertyConfig = ConfigurationProperties.newConfigBuilder<String, String>()
                         .setKey("exist").setDefaultValue("default").build();
            Property<String, String> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.getValue());

            source.setPropertyValue("exist.part1", "ok1");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok1", property.getValue());

            source.setPropertyValue("exist.part1.part2", "ok2");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok2", property.getValue());

            source.setPropertyValue("exist", null);
            source.setPropertyValue("exist.part1.part2", null);
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok1", property.getValue());
        }

        [Fact]
        public void testKeyCachedCascadedProperty()
        {
            MemoryMapConfigurationSource source = createSource();
            source.setPropertyValue("exist", "ok");

            ConfigurationManager manager = createKeyCachedManager(source);

            PropertyConfig<String, String> propertyConfig = ConfigurationProperties.newConfigBuilder<String, String>()
                         .setKey("exist").setDefaultValue("default").build();
            Property<String, String> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.getValue());

            source.setPropertyValue("exist.part1", "ok1");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok1", property.getValue());

            source.setPropertyValue("exist.part1.part2", "ok2");
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok2", property.getValue());

            source.setPropertyValue("exist", null);
            source.setPropertyValue("exist.part1.part2", null);
            Thread.Sleep(10);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok1", property.getValue());
        }
    }
}