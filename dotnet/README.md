# SCF: Simple Configuration Facade

<!-- TOC -->

- [SCF: Simple Configuration Facade](#scf-simple-configuration-facade)
    - [NuGet Package](#nuget-package)
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

## NuGet Package

```sh
dotnet add package MyDotey.SCF -v 1.4.3
dotnet add package MyDotey.SCF.Simple -v 1.4.4
```

## Usage

### Create ConfigurationSource

```c#
PropertiesFileConfigurationSourceConfig sourceConfig = StringPropertySources
    .NewPropertiesFileSourceConfigBuilder().SetName("properties-file").SetFileName(fileName).Build();
PropertiesFileConfigurationSource source = StringPropertySources.NewPropertiesFileSource(sourceConfig);
```

More Sources in StringPropertySources

- EnvironmentVariableConfigurationSource

- MemoryDictionaryConfigurationSource

- PropertiesFileConfigurationSource

### Create ConfigurationManager

```c#
ConfigurationManagerConfig managerConfig = ConfigurationManagers.NewConfigBuilder()
    .SetName("my-conf-manager").AddSource(1, source).Build();
IConfigurationManager manager = ConfigurationManagers.NewManager(managerConfig);
```

### Get Property

```c#
PropertyConfig<string, string> propertyConfig =
    ConfigurationProperties.NewConfigBuilder<string, string>().SetKey("my-property-1")
    .SetDefaultValue("my-default-1").Build();
IProperty<string, string> property = manager.GetProperty(propertyConfig);
Console.WriteLine("key: {0}, value: {1}", property.Config.Key, property.Value);
```

### Get Strong-Typed Property

```java
PropertyConfig<string, int?> propertyConfig =
    ConfigurationProperties.NewConfigBuilder<string, int?>().SetKey("my-property-2")
    .SetDefaultValue(0).AddValueConverter(StringToIntConverter.Default).Build();
IProperty<string, int?> property = manager.GetProperty(propertyConfig);
Console.WriteLine("key: {0}, value: {1}", property.Config.Key, property.Value);
```

### Add Change Listener

```java
property.OnChange += (o, e) => Console.WriteLine("property: {0}, changeTime: {1}, from: {2}, to: {3}",
    e.Property, e.ChangeTime, e.OldValue, e.NewValue);
```

### Add Value Filter

```java
PropertyConfig<string, int?> propertyConfig =
    ConfigurationProperties.NewConfigBuilder<string, int?>().SetKey("my-property-3")
    .SetDefaultValue(0).AddValueConverter(StringToIntConverter.Default)
    .SetValueFilter(v => v >= 0 && v < 100 ? v : null) // range: [0, 100), invalid value is ignored
    .Build();
IProperty<string, int?> property = manager.GetProperty(propertyConfig);
Console.WriteLine("key: {0}, value: {1}", property.Config.Key, property.Value);
```

### Use Properties Facade

```properties
int-value=1
list-value=s1,s2,s3
map-value=k1:v1,k2:v2,k3:v3
int-list-value=1,2,3
int-long-map-value=1:2,3:4,5:6
```

```c#
StringProperties stringProperties = new StringProperties(manager);

IProperty<string, int?> property = stringProperties.GetIntProperty("int-value");
IProperty<string, List<string>> property2 = stringProperties.GetListProperty("list-value");
IProperty<string, Dictionary<string, string>> property3 =
    stringProperties.GetDictionaryProperty("map-value");
IProperty<string, List<int?>> property4 = stringProperties.getListProperty(
    "int-list-value", StringToIntConverter.Default);
IProperty<string, Dictionary<int?, long?>> property5 = stringProperties.GetDictionaryProperty(
    "int-long-map-value", StringToIntConverter.Default, StringToLongConverter.Default);
```

## Extension

### Custom Value Type

```c#
public class MyCustomType
{
    private class MyConverter : StringConverter<MyCustomType>
    {
        public override MyCustomType Convert(string source)
        {
            Dictionary<string, string> fieldValueDictionary =
                StringToDictionaryConverter.Default.Convert(source);
            return new MyCustomType(fieldValueDictionary["name"], fieldValueDictionary["say"],
                StringToIntConverter.Default.Convert(fieldValueDictionary["times"]));
        }
    }

    public static readonly StringConverter<MyCustomType> CONVERTER = new MyConverter();

    public string Name { get; private set; }
    public string Say { get; private set; }
    public int? Times { get; private set; }

    public MyCustomType(string name, string say, int? times)
    {
        Name = name;
        Say = say;
        Times = times;
    }

    public override int GetHashCode()
    {
        int prime = 31;
        int result = 1;
        result = prime * result + ((Name == null) ? 0 : Name.GetHashCode());
        result = prime * result + ((Say == null) ? 0 : Say.GetHashCode());
        result = prime * result + (Times ?? 0);
        return result;
    }

    // for custom type, must override the equals method, so as to know whether a value changed
    public override bool Equals(Object obj)
    {
        if (object.ReferenceEquals(this, obj))
            return true;
        if (obj == null)
            return false;
        if (GetType() != obj.GetType())
            return false;
        MyCustomType other = (MyCustomType)obj;
        if (Name == null)
        {
            if (other.Name != null)
                return false;
        }
        else if (!Name.Equals(other.Name))
            return false;
        if (Say == null)
        {
            if (other.Say != null)
                return false;
        }
        else if (!Say.Equals(other.Say))
            return false;
        if (Times != other.Times)
            return false;
        return true;
    }

    public override string ToString()
    {
        return string.Format("{0} {{ name: {1}, say: {2}, times: {3} }}",
            GetType().Name, Name, Say, Times);
    }
}
```

```c#
IProperty<string, MyCustomType> customTypeProperty =
    stringProperties.GetProperty("my-custom-type-property", MyCustomType.CONVERTER);
```

### Custom Source

```c#
public class ApolloConfigurationSourceConfig : DefaultConfigurationSourceConfig
{
    private IConfig _apolloConfig;

    protected ApolloConfigurationSourceConfig()
    {

    }

    public virtual IConfig ApolloConfig { get { return _apolloConfig; } }

    public new class Builder :
        DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder, ApolloConfigurationSourceConfig>
    {
        protected override ApolloConfigurationSourceConfig NewConfig()
        {
            return new ApolloConfigurationSourceConfig();
        }

        public Builder SetApolloConfig(IConfig config)
        {
            Config._apolloConfig = config;
            return this;
        }

        public override ApolloConfigurationSourceConfig Build()
        {
            if (Config._apolloConfig == null)
                throw new ArgumentNullException("apolloConfig is null");

            return base.Build();
        }
    }
}
```

```c#
public class ApolloConfigurationSource :
    StringPropertyConfigurationSource<ApolloConfigurationSourceConfig>
{
    public ApolloConfigurationSource(ApolloConfigurationSourceConfig config)
        : base(config)
    {
        Config.ApolloConfig.ConfigChanged += (o, e) => RaiseChangeEvent();
    }

    public override string GetPropertyValue(string key)
    {
        return Config.ApolloConfig.GetProperty(key, null);
    }
}
```