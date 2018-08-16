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

        protected virtual TestConfigurationSource CreateSource()
        {
            ConfigurationSourceConfig sourceConfig = ConfigurationSources.NewConfig("test-source");
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

        protected virtual TestDynamicConfigurationSource CreateDynamicSource()
        {
            ConfigurationSourceConfig sourceConfig = ConfigurationSources.NewConfig("test-source");
            Dictionary<string, string> properties = new Dictionary<string, string>();
            properties["exist"] = "ok.2";
            properties["exist2"] = "ok2.2";
            properties["exist3"] = "ok3.2";
            properties["exist4"] = "ok4.2";
            TestDynamicConfigurationSource source = new TestDynamicConfigurationSource(sourceConfig, properties);
            Console.WriteLine("source config: " + sourceConfig + "\n");
            return source;
        }

        protected virtual IConfigurationManager CreateManager(Dictionary<int, IConfigurationSource> sources)
        {
            ConfigurationManagerConfig managerConfig = ConfigurationManagers.NewConfigBuilder().SetName("test")
                    .AddSources(sources).Build();
            Console.WriteLine("manager config: " + managerConfig + "\n");
            return ConfigurationManagers.NewManager(managerConfig);
        }

        [Fact]
        public virtual void TestDuplicatePrioritySource()
        {
            TestConfigurationSource source1 = CreateSource();
            TestDynamicConfigurationSource source2 = CreateDynamicSource();
            Assert.Throws<ArgumentException>(
                () => ConfigurationManagers.NewConfigBuilder().AddSource(1, source1).AddSource(1, source2));
        }

        [Fact()]
        public virtual void TestGetProperties()
        {
            IConfigurationManager manager = CreateManager(new Dictionary<int, IConfigurationSource>() { { 1, CreateSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("not-exist").Build();
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("not-exist2")
                    .SetDefaultValue("default").Build();
            property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("default", property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("exist")
                    .SetDefaultValue("default").Build();
            property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok", property.Value);

            PropertyConfig<string, int> propertyConfig2 = ConfigurationProperties.NewConfigBuilder<string, int>()
                    .SetKey("exist2").Build();
            IProperty<string, int> property2 = manager.GetProperty(propertyConfig2);
            Console.WriteLine("property: " + property2 + "\n");
            Assert.Equal(0, property2.Value);
        }

        [Fact]
        public virtual void TestSameKeyDifferentConfig()
        {
            IConfigurationManager manager = CreateManager(new Dictionary<int, IConfigurationSource>() { { 1, CreateSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("not-exist").Build();
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("not-exist")
                    .SetDefaultValue("default").Build();
            Assert.Throws<ArgumentException>(() => manager.GetProperty(propertyConfig));
        }

        [Fact]
        public virtual void TestSameConfigSameProperty()
        {
            IConfigurationManager manager = CreateManager(new Dictionary<int, IConfigurationSource>() { { 1, CreateSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("not-exist").Build();
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.Value);

            IProperty<string, string> property2 = manager.GetProperty(propertyConfig);
            Console.WriteLine("property2: " + property + "\n");
            Assert.True(Object.ReferenceEquals(property, property2));

            PropertyConfig<string, string> propertyConfig2 = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("not-exist").Build();
            IProperty<string, string> property3 = manager.GetProperty(propertyConfig2);
            Console.WriteLine("property3: " + property2 + "\n");
            Assert.True(Object.ReferenceEquals(property, property3));
        }

        [Fact]
        public virtual void TestGetPropertyWithFilter()
        {
            IConfigurationManager manager = CreateManager(new Dictionary<int, IConfigurationSource>() { { 1, CreateSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("exist").SetValueFilter(v =>
                    {
                        if (Object.Equals("ok", v))
                            return "ok_new";
                        return null;
                    }).Build();
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok_new", property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("exist2")
                    .SetValueFilter(v =>
                    {
                        return v.Length >= 8 && v.Length <= 32 ? v : null;
                    }).Build();
            property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.Value);
        }

        [Fact]
        public virtual void TestGetPropertyWithDiffFilterInSimilarConfig()
        {
            IConfigurationManager manager = CreateManager(new Dictionary<int, IConfigurationSource>() { { 1, CreateSource() } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("exist").SetValueFilter(v =>
                    {
                        if (Object.Equals("ok", v))
                            return "ok_new";
                        return null;
                    }).Build();
            Console.WriteLine("propertyConfig: " + propertyConfig + "\n");
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            Assert.Equal("ok_new", property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("exist")
                    .SetValueFilter(v =>
                    {
                        if (Object.Equals("ok", v))
                            return "ok_new";
                        return null;
                    }).Build();
            Console.WriteLine("propertyConfig: " + propertyConfig + "\n");
            Assert.Throws<ArgumentException>(() => manager.GetProperty(propertyConfig));
        }

        [Fact]
        public virtual void TestGetPropertyWithDynamicSource()
        {
            TestDynamicConfigurationSource source = CreateDynamicSource();
            IConfigurationManager manager = CreateManager(new Dictionary<int, IConfigurationSource>() { { 1, source } });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("exist").Build();
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok.2", property.Value);

            source.SetPropertyValue("exist", "okx");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("okx", property.Value);

            source.SetPropertyValue("exist", "ok.2");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok.2", property.Value);

            ObjectReference<bool> touched = new ObjectReference<bool>();
            property.AddChangeListener(p => touched.Value = true);
            property.AddChangeListener(e => Console.WriteLine("property: {0}, changeTime: {1}, from: {2}, to: {3}\n",
                    e.Property, e.ChangeTime, e.OldValue, e.NewValue));
            source.SetPropertyValue("exist", "okx");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("okx", property.Value);
            Assert.True(touched.Value);
        }

        [Fact]
        public virtual void TestGetPropertiesMultipleSource()
        {
            TestConfigurationSource source1 = CreateSource();
            TestDynamicConfigurationSource source2 = CreateDynamicSource();
            IConfigurationManager manager = CreateManager(
                new Dictionary<int, IConfigurationSource>()
                {
                    { 1, source1 },
                    { 2, source2 },
                });
            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("not-exist").Build();
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Null(property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("not-exist2")
                    .SetDefaultValue("default").Build();
            property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("default", property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("exist")
                    .SetDefaultValue("default").Build();
            property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok.2", property.Value);

            propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("exist5")
                    .SetDefaultValue("default").Build();
            property = manager.GetProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok5", property.Value);

            source2.SetPropertyValue("exist5", "ok5.2");
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok5.2", property.Value);

            source2.SetPropertyValue("exist5", null);
            Console.WriteLine("property: " + property + "\n");
            Assert.Equal("ok5", property.Value);
        }

        [Fact]
        public virtual void TestChangeListener()
        {
            TestDynamicConfigurationSource source = CreateDynamicSource();
            IConfigurationManager manager = CreateManager(new Dictionary<int, IConfigurationSource>() { { 1, source } });
            ObjectReference<int> changeCount = new ObjectReference<int>();
            manager.AddChangeListener(e =>
            {
                changeCount.Value = changeCount.Value + 1;
                Console.WriteLine("property changed: " + e);
            });

            PropertyConfig<string, string> propertyConfig = ConfigurationProperties.NewConfigBuilder<string, string>()
                    .SetKey("exist").Build();
            IProperty<string, string> property = manager.GetProperty(propertyConfig);
            ObjectReference<int> changeCount2 = new ObjectReference<int>();
            property.AddChangeListener(p => changeCount2.Value = changeCount2.Value + 1);

            source.SetPropertyValue("exist", "okx");
            Assert.Equal(1, changeCount.Value);
            Assert.Equal(1, changeCount2.Value);

            source.SetPropertyValue("exist", "ok.2");
            Assert.Equal(2, changeCount.Value);
            Assert.Equal(2, changeCount2.Value);

            source.SetPropertyValue("exist", "okx");
            Assert.Equal(3, changeCount.Value);
            Assert.Equal(3, changeCount2.Value);

            // value not change, no change event
            source.SetPropertyValue("exist", "okx");
            Assert.Equal(3, changeCount.Value);
            Assert.Equal(3, changeCount2.Value);
        }

        [Fact]
        public virtual void TestEquality()
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

            public virtual T Value { get { return (T)_value; } set { _value = value; } }

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