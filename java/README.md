# SCF: Simple Configuration Facade

<!-- TOC -->

- [SCF: Simple Configuration Facade](#scf-simple-configuration-facade)
    - [Requirements](#requirements)
    - [Maven Dependency](#maven-dependency)
    - [Usage](#usage)
        - [Create ConfigurationSource](#create-configurationsource)
        - [Create ConfigurationManager](#create-configurationmanager)
        - [Get Property](#get-property)
        - [Get Strong-Typed Property](#get-strong-typed-property)
        - [Add Change Listener](#add-change-listener)
        - [Add Value Filter](#add-value-filter)
        - [Use Properties Facade](#use-properties-facade)
    - [Extension](#extension)
        - [Custom Value Type](#custom-value-type)
        - [Custom Source](#custom-source)

<!-- /TOC -->

## Requirements

- **Java 8**

## Maven Dependency

```xml
<dependency>
    <groupId>org.mydotey.scf</groupId>
    <artifactId>scf-core</artifactId>
    <version>1.3.0</version>
</dependency>
<dependency>
    <groupId>org.mydotey.scf</groupId>
    <artifactId>scf-simple</artifactId>
    <version>1.3.0</version>
</dependency>
```

## Usage

### Create ConfigurationSource

```java
PropertiesFileConfigurationSourceConfig sourceConfig = StringPropertySources
        .newPropertiesFileSourceConfigBuilder().setName("properties-file").setFileName(fileName).build();
PropertiesFileConfigurationSource source = StringPropertySources.newPropertiesFileSource(sourceConfig);
```

More Sources in StringPropertiesSources

- EnvironmentVariableConfigurationSource

- SystemPropertiesConfigurationSource

- MemoryMapConfigurationSource

- PropertiesFileConfigurationSource

### Create ConfigurationManager

```java
ConfigurationManagerConfig managerConfig = ConfigurationManagers.newConfigBuilder()
        .setName("my-conf-manager").addSource(1, source).build(); // 1 source with priority 1
ConfigurationManager manager = ConfigurationManagers.newManager(managerConfig);
```

### Get Property

```java
PropertyConfig<String, String> propertyConfig =
        ConfigurationProperties.<String, String> newConfigBuilder().setKey("my-property-1")
        .setValueType(String.class).setDefaultValue("my-default-1").build();
Property<String, String> property = manager.getProperty(propertyConfig);
System.out.printf("key: %s, value: %s\n", property.getConfig().getKey(), property.getValue());
```

### Get Strong-Typed Property

```java
PropertyConfig<String, Integer> propertyConfig =
        ConfigurationProperties.<String, Integer> newConfigBuilder().setKey("my-property-2")
        .setValueType(Integer.class).setDefaultValue(0)
        .addValueConverter(StringToIntConverter.DEFAULT).build();
Property<String, Integer> property = manager.getProperty(propertyConfig);
System.out.printf("key: %s, value: %d\n", property.getConfig().getKey(), property.getValue());
```

### Add Change Listener

```java
property.addChangeListener(e -> System.out.printf("property: %s, changeTime: %s, from: %s, to: %s\n",
        e.getProperty(), e.getChangeTime(), e.getOldValue(), e.getNewValue()));
```

### Add Value Filter

```java
PropertyConfig<String, Integer> propertyConfig =
        ConfigurationProperties.<String, Integer> newConfigBuilder()
        .setKey("my-property-3").setValueType(Integer.class).setDefaultValue(0)
        .addValueConverter(StringToIntConverter.DEFAULT)
        .setValueFilter(v -> v >= 0 && v < 100 ? v : null) // range: [0, 100), invalid value is ignored
        .build();
Property<String, Integer> property = manager.getProperty(propertyConfig);
System.out.printf("key: %s, value: %d\n", property.getConfig().getKey(), property.getValue());
```

### Use Properties Facade

```properties
int-value=1
list-value=s1,s2,s3
map-value=k1:v1,k2:v2,k3:v3
int-list-value=1,2,3
int-long-map-value=1:2,3:4,5:6
```

```java
StringProperties stringProperties = new StringProperties(manager);

Property<String, Integer> property = stringProperties.getIntProperty("int-value");
Property<String, List<String>> property2 = stringProperties.getListProperty("list-value");
Property<String, Map<String, String>> property3 = stringProperties.getMapProperty("map-value");
Property<String, List<Integer>> property4 = stringProperties.getListProperty("int-list-value",
        StringToIntConverter.DEFAULT);
Property<String, Map<Integer, Long>> property5 = stringProperties.getMapProperty("int-long-map-value",
        StringToIntConverter.DEFAULT, StringToLongConverter.DEFAULT);
```

## Extension

### Custom Value Type

```java
public class MyCustomType {
    public static final StringConverter<MyCustomType> CONVERTER = new StringConverter<MyCustomType>(
            MyCustomType.class) {
        @Override
        public MyCustomType convert(String source) {
            Map<String, String> fieldValueMap = StringToMapConverter.DEFAULT.convert(source);
            return new MyCustomType(fieldValueMap.get("name"), fieldValueMap.get("say"),
                    StringToIntConverter.DEFAULT.convert(fieldValueMap.get("times")));
        }
    };

    private String name;
    private String say;
    private int times;

    public MyCustomType(String name, String say, int times) {
        super();
        this.name = name;
        this.say = say;
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public String getSay() {
        return say;
    }

    public int getTimes() {
        return times;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((say == null) ? 0 : say.hashCode());
        result = prime * result + times;
        return result;
    }

    // for custom type, must override the equals method, so as to know whether a value changed
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyCustomType other = (MyCustomType) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (say == null) {
            if (other.say != null)
                return false;
        } else if (!say.equals(other.say))
            return false;
        if (times != other.times)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s { name: %s, say: %s, times: %d }",
            getClass().getSimpleName(), name, say, times);
    }
}
```

```java
Property<String, MyCustomType> customTypeProperty =
    stringProperties.getProperty("my-custom-type-property", MyCustomType.CONVERTER);
```

### Custom Source

```java
public class ApolloConfigurationSourceConfig extends DefaultConfigurationSourceConfig {

    private Config _apolloConfig;

    protected ApolloConfigurationSourceConfig() {

    }

    public Config getApolloConfig() {
        return _apolloConfig;
    }

    public static class Builder extends DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder> {

        @Override
        protected DefaultConfigurationSourceConfig newConfig() {
            return new ApolloConfigurationSourceConfig();
        }

        @Override
        protected ApolloConfigurationSourceConfig getConfig() {
            return (ApolloConfigurationSourceConfig) super.getConfig();
        }

        public Builder setApolloConfig(Config config) {
            getConfig()._apolloConfig = config;
            return this;
        }

        @Override
        public ApolloConfigurationSourceConfig build() {
            Objects.requireNonNull(getConfig()._apolloConfig, "apolloConfig is null");

            return (ApolloConfigurationSourceConfig) super.build();
        }

    }

}
```

```java
public class ApolloConfigurationSource extends StringPropertyConfigurationSource {

    public ApolloConfigurationSource(ApolloConfigurationSourceConfig config) {
        super(config);
        getConfig().getApolloConfig().addChangeListener(
            e -> ApolloConfigurationSource.this.raiseChangeEvent());
    }

    @Override
    public ApolloConfigurationSourceConfig getConfig() {
        return (ApolloConfigurationSourceConfig) super.getConfig();
    }

    @Override
    public String getPropertyValue(String key) {
        return getConfig().getApolloConfig().getProperty(key, null);
    }

}
```