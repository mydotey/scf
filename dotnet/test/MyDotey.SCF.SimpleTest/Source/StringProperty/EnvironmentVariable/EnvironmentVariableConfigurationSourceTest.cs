using System;

using Xunit;
using MyDotey.SCF.Facade;

namespace MyDotey.SCF.Source.StringProperty.EnvironmentVariable
{
    /**koqizhao
     * @author 
     *
     * May 22, 2018
     */
    public class EnvironmentVariableConfigurationSourceTest
    {
        protected ConfigurationManager createManager()
        {
            ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder().setName("test")
                    .addSource(1, StringPropertySources.newEnvironmentVariableSource("environment-variable")).build();
            Console.WriteLine("manager config: " + managerConfig + "\n");
            return ConfigurationManagers.newManager(managerConfig);
        }

        [Fact]
        public void testGetProperties()
        {
            ConfigurationManager manager = createManager();
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

            propertyConfig = ConfigurationProperties.newConfigBuilder<String, String>().setKey("PATH")
                    .setDefaultValue("default").build();
            property = manager.getProperty(propertyConfig);
            Console.WriteLine("property: " + property + "\n");
            Assert.NotNull(property.getValue());
            Assert.NotEqual("default", property.getValue());
        }
    }
}