using System;
using System.Collections.Generic;

using Xunit;
using NLog;
using NLog.Config;
using NLog.Targets;
using MyDotey.SCF.Type;
using MyDotey.SCF.Type.String;
using MyDotey.SCF.Source.StringProperty.PropertiesFile;

namespace MyDotey.SCF.Facade
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class StringPropertiesTest
    {
        public StringPropertiesTest()
        {
            var config = new LoggingConfiguration();
            var logconsole = new ConsoleTarget() { Name = "logconsole" };
            config.LoggingRules.Add(new NLog.Config.LoggingRule("*", LogLevel.Trace, logconsole));
            NLog.LogManager.Configuration = config;
        }
 
        protected ConfigurationManager createManager(String fileName)
        {
            PropertiesFileConfigurationSourceConfig sourceConfig = StringPropertySources
                    .newPropertiesFileSourceConfigBuilder().setName("properties-source").setFileName(fileName).build();
            Console.WriteLine("source config: " + sourceConfig + "\n");
            ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                    .addSource(1, StringPropertySources.newPropertiesFileSource(sourceConfig)).build();
            Console.WriteLine("manager config: " + managerConfig + "\n");
            return ConfigurationManagers.newManager(managerConfig);
        }

        protected StringProperties createStringProperties(String fileName)
        {
            ConfigurationManager manager = createManager(fileName);
            return new StringProperties(manager);
        }

        [Fact]
        public void testGetProperties()
        {
            StringProperties stringProperties = createStringProperties("test.properties");
            Property<String, String> property = stringProperties.getStringProperty("not-exist");
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.getValue());

            property = stringProperties.getStringProperty("not-exist2", "default");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("default", property.getValue());

            property = stringProperties.getStringProperty("exist", "default");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.getValue());
        }

        [Fact]
        public void testGetTypedProperties()
        {
            StringProperties stringProperties = createStringProperties("test.properties");
            Property<String, int?> property = stringProperties.getIntProperty("int-value");
            Console.WriteLine("property: " + property + "\n");
            int? expected = 1;
            Assert.Equal(expected, property.getValue());

            Property<String, List<String>> property2 = stringProperties.getListProperty("list-value");
            Console.WriteLine("property: " + property2 + "\n");
            List<String> expected2 = new List<string>() { "s1", "s2", "s3" };
            Assert.Equal(expected2, property2.getValue());

            Property<String, Dictionary<String, String>> property3 = stringProperties.getDictionaryProperty("map-value");
            Console.WriteLine("property: " + property3 + "\n");
            Dictionary<String, String> expected3 = new Dictionary<String, String>()
            {
                { "k1", "v1" },
                { "k2", "v2" },
                { "k3", "v3" }
            };
            Assert.Equal(expected3, property3.getValue());

            Property<String, List<int?>> property4 = stringProperties.getListProperty("int-list-value",
                    StringToIntConverter.DEFAULT);
            Console.WriteLine("property: " + property4 + "\n");
            List<int?> expected4 = new List<int?>() { 1, 2, 3 };
            Assert.Equal(expected4, property4.getValue());

            Property<String, Dictionary<int?, long?>> property5 = stringProperties.getDictionaryProperty("int-long-map-value",
                    StringToIntConverter.DEFAULT, StringToLongConverter.DEFAULT);
            Console.WriteLine("property: " + property5 + "\n");
            Dictionary<int?, long?> expected5 = new Dictionary<int?, long?>()
            {
                { 1, 2L },
                { 3, 4L },
                { 5, 6L }
            };
            Assert.Equal(expected5, property5.getValue());
        }

        [Fact]
        public void testSameKeyDifferentConfig()
        {
            StringProperties stringProperties = createStringProperties("test.properties");
            Property<String, Dictionary<String, String>> property = stringProperties.getDictionaryProperty("map-value");
            Dictionary<String, String> expected = new Dictionary<String, String>()
            {
                { "k1", "v1" },
                { "k2", "v2" },
                { "k3", "v3" }
            };
            Assert.Equal(expected, property.getValue());

            Assert.Throws<ArgumentException>(
                () => stringProperties.getDictionaryProperty("map-value", StringToIntConverter.DEFAULT, StringToLongConverter.DEFAULT));
        }

        [Fact]
        public void testSameConfigSameProperty()
        {
            StringProperties stringProperties = createStringProperties("test.properties");
            Property<String, Dictionary<String, String>> property = stringProperties.getDictionaryProperty("map-value");
            Dictionary<String, String> expected = new Dictionary<String, String>()
            {
                { "k1", "v1" },
                { "k2", "v2" },
                { "k3", "v3" }
            };
            Assert.Equal(expected, property.getValue());

            Property<String, Dictionary<String, String>> property2 = stringProperties.getDictionaryProperty("map-value");
            Console.WriteLine("property2: " + property + "\n");
            Assert.True(property == property2);
        }
    }
}