using System;
using System.Collections.Generic;
using System.Linq;

using MyDotey.SCF.Facade;
using Xunit;
using NLog;
using NLog.Config;
using NLog.Targets;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class ConfigurationManagerTest
    {
        public ConfigurationManagerTest()
        {
            var config = new LoggingConfiguration();
            var logconsole = new ConsoleTarget() { Name = "logconsole" };
            config.LoggingRules.Add(new NLog.Config.LoggingRule("*", LogLevel.Trace, logconsole));
            NLog.LogManager.Configuration = config;
        }

        protected TestConfigurationSource createSource()
        {
            ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfig("test-source");
            Dictionary<string, string> properties = new Dictionary<string, string>();
            properties["exist"] = "ok";
            properties["exist2"] = "ok2";
            properties["exist3"] = "ok3";
            properties["exist4"] = "ok4";
            properties["exist5"] = "ok5";
            TestConfigurationSource source = new TestConfigurationSource(sourceConfig, properties);
            Console.WriteLine("source config: " + sourceConfig + "\n");
            return source;
        }

        protected TestDynamicConfigurationSource createDynamicSource()
        {
            ConfigurationSourceConfig sourceConfig = ConfigurationSources.newConfig("test-source");
            Dictionary<string, string> properties = new Dictionary<string, string>();
            properties["exist"] = "ok.2";
            properties["exist2"] = "ok2.2";
            properties["exist3"] = "ok3.2";
            properties["exist4"] = "ok4.2";
            TestDynamicConfigurationSource source = new TestDynamicConfigurationSource(sourceConfig, properties);
            Console.WriteLine("source config: " + sourceConfig + "\n");
            return source;
        }

        protected ConfigurationManager createManager(Dictionary<int, ConfigurationSource> sources)
        {
            ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                    .addSources(sources).build();
            Console.WriteLine("manager config: " + managerConfig + "\n");
            return ConfigurationManagers.newManager(managerConfig);
        }

        [Fact]
        public void testDuplicatePrioritySource()
        {
            TestConfigurationSource source1 = createSource();
            TestDynamicConfigurationSource source2 = createDynamicSource();
            Assert.Throws<ArgumentException>(
                () => ConfigurationManagers.newConfigBuilder().addSource(1, source1).addSource(1, source2));
        }

        [Fact()]
        public void testGetProperties()
        {
            ConfigurationManager manager = createManager(new Dictionary<int, ConfigurationSource>() { { 1, createSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("not-exist").build();
            Property<string, string> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("not-exist2")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("default", property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("exist")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.getValue());

            PropertyConfig<string, int> propertyConfig2 = ConfigurationProperties.newConfigBuilder<string, int>()
                    .setKey("exist2").build();
            Property<string, int> property2 = manager.getProperty(propertyConfig2);
            Console.WriteLine("property: " + property2 + "\n");
            Assert.Equal(0, property2.getValue());
        }

        [Fact]
        public void testSameKeyDifferentConfig()
        {
            ConfigurationManager manager = createManager(new Dictionary<int, ConfigurationSource>() { { 1, createSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("not-exist").build();
            Property<string, string> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("not-exist")
                    .setDefaultValue("default").build();
            Assert.Throws<ArgumentException>(() => manager.getProperty(propertyConfig));
        }

        [Fact]
        public void testSameConfigSameProperty()
        {
            ConfigurationManager manager = createManager(new Dictionary<int, ConfigurationSource>() { { 1, createSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("not-exist").build();
            Property<string, string> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.getValue());

            Property<string, string> property2 = manager.getProperty(propertyConfig);
            Console.WriteLine("property2: " + property + "\n");
            Assert.True(Object.ReferenceEquals(property, property2));

            PropertyConfig<string, string> propertyConfig2 = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("not-exist").build();
            Property<string, string> property3 = manager.getProperty(propertyConfig2);
            Console.WriteLine("property3: " + property2 + "\n");
            Assert.True(Object.ReferenceEquals(property, property3));
        }

        [Fact]
        public void testGetPropertyWithFilter()
        {
            ConfigurationManager manager = createManager(new Dictionary<int, ConfigurationSource>() { { 1, createSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("exist").setValueFilter(v =>
                    {
                        if (Object.Equals("ok", v))
                            return "ok_new";
                        return null;
                    }).build();
            Property<string, string> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok_new", property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("exist2")
                    .setValueFilter(v =>
                    {
                        return v.Length >= 8 && v.Length <= 32 ? v : null;
                    }).build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.getValue());
        }

        [Fact]
        public void testGetPropertyWithDiffFilterInSimilarConfig()
        {
            ConfigurationManager manager = createManager(new Dictionary<int, ConfigurationSource>() { { 1, createSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("exist").setValueFilter(v =>
                    {
                        if (Object.Equals("ok", v))
                            return "ok_new";
                        return null;
                    }).build();
            Console.WriteLine("propertyConfig: " + propertyConfig + "\n");
            Property<string, string> property = manager.getProperty(propertyConfig);
            Assert.Equal("ok_new", property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("exist")
                    .setValueFilter(v =>
                    {
                        if (Object.Equals("ok", v))
                            return "ok_new";
                        return null;
                    }).build();
            Console.WriteLine("propertyConfig: " + propertyConfig + "\n");
            Assert.Throws<ArgumentException>(() => manager.getProperty(propertyConfig));
        }

        [Fact]
        public void testGetPropertyWithDynamicSource()
        {
            TestDynamicConfigurationSource source = createDynamicSource();
            ConfigurationManager manager = createManager(new Dictionary<int, ConfigurationSource>() { { 1, source } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("exist").build();
            Property<string, string> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok.2", property.getValue());

            source.setPropertyValue("exist", "okx");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("okx", property.getValue());

            source.setPropertyValue("exist", "ok.2");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok.2", property.getValue());

            ObjectReference<bool> touched = new ObjectReference<bool>();
            property.addChangeListener(p => touched.Value = true);
            property.addChangeListener(e => Console.WriteLine("property: {0}, changeTime: {1}, from: {2}, to: {3}\n",
                    e.getProperty(), e.getChangeTime(), e.getOldValue(), e.getNewValue()));
            source.setPropertyValue("exist", "okx");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("okx", property.getValue());
            Assert.True(touched.Value);
        }

        [Fact]
        public void testGetPropertiesMultipleSource()
        {
            TestConfigurationSource source1 = createSource();
            TestDynamicConfigurationSource source2 = createDynamicSource();
            ConfigurationManager manager = createManager(
                new Dictionary<int, ConfigurationSource>()
                {
                    { 1, source1 },
                    { 2, source2 },
                });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("not-exist").build();
            Property<string, string> property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("not-exist2")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("default", property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("exist")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok.2", property.getValue());

            propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>().setKey("exist5")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok5", property.getValue());

            source2.setPropertyValue("exist5", "ok5.2");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok5.2", property.getValue());

            source2.setPropertyValue("exist5", null);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok5", property.getValue());
        }

        [Fact]
        public void testChangeListener()
        {
            TestDynamicConfigurationSource source = createDynamicSource();
            ConfigurationManager manager = createManager(new Dictionary<int, ConfigurationSource>() { { 1, source } });
            ObjectReference<int> changeCount = new ObjectReference<int>();
            manager.addChangeListener(e =>
            {
                changeCount.Value = changeCount.Value + 1;
                Console.WriteLine("property changed: " + e);
            });

            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.newConfigBuilder<string, string>()
                    .setKey("exist").build();
            Property<string, string> property = manager.getProperty(propertyConfig);
            ObjectReference<int> changeCount2 = new ObjectReference<int>();
            property.addChangeListener(p => changeCount2.Value = changeCount2.Value + 1);

            source.setPropertyValue("exist", "okx");
            Assert.Equal(1, changeCount.Value);
            Assert.Equal(1, changeCount2.Value);

            source.setPropertyValue("exist", "ok.2");
            Assert.Equal(2, changeCount.Value);
            Assert.Equal(2, changeCount2.Value);

            source.setPropertyValue("exist", "okx");
            Assert.Equal(3, changeCount.Value);
            Assert.Equal(3, changeCount2.Value);

            // value not change, no change event
            source.setPropertyValue("exist", "okx");
            Assert.Equal(3, changeCount.Value);
            Assert.Equal(3, changeCount2.Value);
        }

        [Fact]
        public void testEquality()
        {
            var l1 = new List<String>() { "1", "2" };
            var l2 = new List<String>() { "1", "2" };
            Assert.False(l1.Equals(l2));
            Assert.True(l1.SequenceEqual(l2));
            Assert.NotEqual(l1.GetHashCode(), l2.GetHashCode());
            Assert.Equal(l1.HashCode(), l2.HashCode());

            var k1 = new Dictionary<String, String>() { { "1", "2" } };
            var k2 = new Dictionary<String, String>() { { "1", "2" } };
            Assert.False(k1.Equals(k2));
            Assert.True(k1.SequenceEqual(k2));
            Assert.NotEqual(k1.GetHashCode(), k2.GetHashCode());
            Assert.Equal(k1.HashCode(), k2.HashCode());

            var p1 = new KeyValuePair<String, String>("1", "2");
            var p2 = new KeyValuePair<String, String>("1", "2");
            Assert.True(p1.Equals(p2));
            Assert.Equal(p1.GetHashCode(), p2.GetHashCode());

            List<String> l3 = null;
            Assert.False(l1.Equal(l3));
            Assert.False(l3.Equal(l1));
            Assert.True(l3.Equal(l3));
        }

        public class ObjectReference<T>
        {
            private volatile object _value;

            public T Value { get { return (T)_value; } set { _value = value; } }

            public ObjectReference()
                : this(default(T))
            {
            }

            public ObjectReference(T value)
            {
                _value = value;
            }
        }
    }
}