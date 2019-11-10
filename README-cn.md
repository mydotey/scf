# SCF: 简单配置门面

[[English](https://github.com/mydotey/scf)]&nbsp;&nbsp;[[中文](https://github.com/mydotey/scf/blob/master/README-cn.md)]

Simple Configuration Facade, 简写为 [SCF](https://github.com/mydotey/scf)。是 **代码** 和 **外部配置** (properties文件, 环境变量，系统/命令行参数, yaml文件, 等等)之间的一层抽象. 命名上和另一个著名组件[slf4j](https://www.slf4j.org/) (Simple Logging Facade for Java)相似, 在配置领域的地位也和slf4j （.NET可类比NLog）在日志领域的地位相同。

SCF使得代码和外部配置分离。代码使用一个配置项，无需关心配置项在哪里配置，如何配置。

![arch](https://raw.githubusercontent.com/mydotey/scf/master/resources/images/scf.png)

## 用法

- java: [https://github.com/mydotey/scf/tree/master/java](https://github.com/mydotey/scf/tree/master/java)

- dotnet: [https://github.com/mydotey/scf/tree/master/dotnet](https://github.com/mydotey/scf/tree/master/dotnet)

- rust: [https://github.com/mydotey/scf/tree/master/rust](https://github.com/mydotey/scf/tree/master/rust)

## 示例

[https://github.com/mydotey/scf-best-practice](https://github.com/mydotey/scf-best-practice)

## 功能

### 强类型

核心抽象是强类型的Property<K, V>, Key和Value都是强类型。

### 安全的配置

可以通过1个value filter来自动检查外部配置的正确性。

### 动态配置

配置可以是动态变化的。配置项的值自动更新。用户也可添加Listener监听配置项的变化。

### 多配置源

多个配置源有不同的优先级。配置项的值按配置源的优先级自动计算。

### 可扩展

**scf-core** 里只定义了一组接口抽象和默认实现。所有核心概念都是可扩展的（配置管理器，配置源，配置项等等）。用户可以自由地对默认实现进行扩展，或者编写自己的实现。

一些常用扩展：

- [scf-simple](https://github.com/mydotey/scf-simple)

- [scf-labeled](https://github.com/mydotey/scf-labeled)

- [scf-yaml](https://github.com/mydotey/scf-yaml)

- [scf-apollo](https://github.com/mydotey/scf-apollo)

### 轻量

无线程使用，只使用少量内存来缓存配置项。

### 支持多线程并发

Manager/Property的方法都是线程安全的，可以多线程并发使用。时间复杂读为O(1)，和ConcurrentHashMap相同。

### 容易使用

**scf-simple** 对最简单常用的`Property<String, String>`场景进行了实现: properties file, memory Map, system properties, env variables.

参考:

- [用法](#用法)

- [示例](#示例)

## 核心概念

### 配置项（Property）

配置项是可以在*代码*里独立使用的配置单元，有1个唯一的*Property Config*和1个*强类型*值。

*代码*可以监听配置项的动态变化。配置项的值由*配置管理器*（Configuration Manager）自动更新。

### 配置项配置（Property Config）

配置项配置具有以下组成部分：

- **Key**: 在1个*配置管理器*里唯一标识1个*配置项*。最常见的类型是*String*，也可以是其它*强类型*的对象。

- **Value Type**: 配置项的值的类型。

- **Default Value**: *配置项*的默认值。如果配置项在任何*配置源*里都没有配，使用此默认配置。

- **Value Converter**: 值转换器。把1个类型的值转换为另1个类型的值。比如1个配置源里有配置项<K, V1>，但代码需要的配置是<K, V2>，值转换器<V1, V2>可以自动地把V1的值转为V2的值。

- **Value Filter**: 值过滤器。主要用于*检查*配置源里取到的值的有效性。

- **Value Comparator**: 值比较器。用于*比较*配置项的值是否有变化。默认使用值的`Equals`方法进行比较。

- **Is Static**: 属性是否可以动态改变。false时属性初始化后可以动态变化，true时属性初始化后不再变化。默认为false。

- **Is Required**: 属性是否必须配置或必须设定默认值。默认为false。为true时，如果忘记配置且无默认值，获取属性时会抛出异常。

- **Doc**: 属性描述文档。可用于生成应用的配置文档。

### 配置源（Configuration Source）

1个配置项可以以多种形式来进行配置。例如内存字典、properties文件、环境变量、命令行参数、yaml文件等等。

多种配置方式可以一起使用。每种配置方式是1个配置源。配置源负责对配置项提供值。

有时1个配置源无法向某个配置项提供值，提供null（认为没有配置此配置项）。

- 配置项的Key无法被配置源识别。如key是1个强类型的对象`{ key: request.timeout, labels: { dc: aws-us-east1, app: 100000 } }`, 但是配置源只接受String类型的key。

- 配置项在配置源里没有配置。

- 配置项在配置源里的值是类型A，但代码里需要类型B，PropertyConfig没有值转换器可以把类型A转为类型B，配置源也无法自动把A转换为B。

### 配置管理器（Configuration Manager）

配置管理器是*代码*和*外部配置*间的门面。*代码*从*配置管理器*获取*配置项*，不关心配置项在哪里配置，如何配置。

1个程序里可以只使用1个配置管理器，也可以使用多个不同的管理器。不同的组件可以使用不同的配置管理器，配置管理器也可以在不同组件间共享/传递。

配置管理器提供2个Api:

- `<K, V> Property<K, V> getProperty(PropertyConfig<K, V> config)`: 用于具有 **稳定** 的Key的 **稳定** 的配置项，返回1个 **唯一** 的配置项。 **配置管理器** 保持这个配置项，自动更新配置值，通知配置监听器配置项的变化。 **代码** 可以保持获得的配置项，多次重复使用。

- `<V> V getPropertyValue(PropertyConfig<K, V> config)`: 用于具有 **不稳定** 的Key的 **不稳定** 的配置项。例如, 访问者 **IP** 作为配置项Key的一部分, 不确定程序里有多少配置项，不确定哪个配置项会被配置，何时会被配置。

### 配置源和配置优先级

1个配置管理器可以管理多个配置源， **不同的配置源具有不同的优先级** 。配置管理器按优先级从配置源获取配置值。

### 核心概念间的关系

![arch-class](https://raw.githubusercontent.com/mydotey/scf/master/resources/images/scf-class.png)

## 核心逻辑

![get-property-value](https://raw.githubusercontent.com/mydotey/scf/master/resources/images/get-property-value.png)

## 开发者

- Qiang Zhao <koqizhao@outlook.com>
