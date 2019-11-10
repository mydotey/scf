
#[macro_use]
extern crate lang_extension;

use std::collections::HashMap;
use std::sync::Arc;
use std::sync::atomic::*;

use lang_extension::convert::*;

use scf_core::source::*;
use scf_core::manager::*;
use scf_core::facade::*;

mod test_configuration_source;
use test_configuration_source::*;

mod test_dynamic_configuration_source;
use test_dynamic_configuration_source::*;

fn create_source() -> TestConfigurationSource {
    let source_config = ConfigurationSources::new_config("test-source");
    let mut properties = HashMap::<String, String>::new();
    properties.insert("exist".to_string(), "ok".to_string());
    properties.insert("exist2".to_string(), "ok2".to_string());
    properties.insert("exist3".to_string(), "ok3".to_string());
    properties.insert("exist4".to_string(), "ok4".to_string());
    properties.insert("exist5".to_string(), "ok5".to_string());
    properties.insert("exist_int".to_string(), "1".to_string());
    println!("source config: {:?}\n", source_config);
    TestConfigurationSource::new(source_config, properties)
}

fn create_dynamic_source() -> TestDynamicConfigurationSource {
    let source_config = ConfigurationSources::new_config("test-source");
    let mut properties = HashMap::<String, String>::new();
    properties.insert("exist".to_string(), "ok.2".to_string());
    properties.insert("exist2".to_string(), "ok2.2".to_string());
    properties.insert("exist3".to_string(), "ok3.2".to_string());
    properties.insert("exist4".to_string(), "ok4.2".to_string());
    println!("source config: {:?}\n", source_config);
    TestDynamicConfigurationSource::new(source_config, properties)
}

fn create_manager(sources: HashMap<i32, Box<dyn ConfigurationSource>>) -> Box<dyn ConfigurationManager> {
    let manager_config = ConfigurationManagers::new_config_builder().set_name("test")
        .add_sources(sources).build();
    println!("manager config: {:?}\n", manager_config);
    ConfigurationManagers::new_manager(manager_config)
}

fn create_properties(sources: HashMap<i32, Box<dyn ConfigurationSource>>) -> ConfigurationProperties {
    let manager = create_manager(sources);
    ConfigurationProperties::new(manager)
}

fn create_simple_properties() -> (ConfigurationProperties, TestConfigurationSource) {
    let mut sources = HashMap::new();
    let source = create_source();
    sources.insert(1, ConfigurationSource::to_boxed(source.clone()));
    (create_properties(sources), source)
}

fn create_simple_dynamic_properties() -> (ConfigurationProperties, TestDynamicConfigurationSource) {
    let mut sources = HashMap::new();
    let source = create_dynamic_source();
    sources.insert(1, ConfigurationSource::to_boxed(source.clone()));
    (create_properties(sources), source)
}

fn create_complex_properties() ->
    (ConfigurationProperties, TestConfigurationSource, TestDynamicConfigurationSource) {
    let mut sources = HashMap::new();
    let source1 = create_source();
    let source2 = create_dynamic_source();
    sources.insert(1, ConfigurationSource::to_boxed(source1.clone()));
    sources.insert(2, ConfigurationSource::to_boxed(source2.clone()));
    (create_properties(sources), source1, source2)
}

fn new_value_converter() -> DefaultTypeConverter<String, i32> {
    DefaultTypeConverter::<String, i32>::new(Box::new(|s|{
        match s.parse::<i32>() {
            Ok(v) => {
                println!("parse value: {}", v);
                Ok(Box::new(v))
            },
            Err(e) => {
                println!("parse error: {}", e);
                Err(Box::new(e.to_string()))
            }
        }
    }))
}

#[should_panic]
#[test]
fn test_duplicate_priority_source() {
    let source1 = Box::new(create_source());
    let source2 = Box::new(create_dynamic_source());
    ConfigurationManagers::new_config_builder().add_source(1, source1).add_source(1, source2);
}

#[test]
fn test_get_properties() {
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist".to_string())).build();
    let mut property = properties.get_property::<String, String>(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(None, property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist2".to_string()))
        .set_default_value(Box::new("default".to_string())).build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("default".to_string())), property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist".to_string()))
        .set_default_value(Box::new("default".to_string())).build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok".to_string())), property.get_value());

    let property_config2 = ConfigurationProperties::new_config_builder::<String, i32>()
        .set_key(Box::new("exist2".to_string())).build();
    let property2 = properties.get_property(property_config2.as_ref());
    println!("property: {:?}\n", property2);
    assert_eq!(None, property2.get_value());
}

#[should_panic]
#[test]
fn test_same_key_different_config() {
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist".to_string())).build();
    let property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(None, property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist".to_string()))
        .set_default_value(Box::new("default".to_string())).build();
    properties.get_property(property_config.as_ref());
}
 
#[test]
fn test_same_config_same_property() {
    let (properties, _) = create_simple_properties();
    let property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist".to_string())).build();
    let property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(None, property.get_value());

    let property2 = properties.get_property(property_config.as_ref());
    println!("property2: {:?}\n", property2);
    assert_eq!(&property, &property2);

    let property_config2 = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist".to_string())).build();
    let property3 = properties.get_property(property_config2.as_ref());
    println!("property3: {:?}\n", property3);
    assert_eq!(&property, &property3);
}

#[test]
fn test_get_property_with_converter() {
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, i32>()
        .set_key(Box::new("exist_int".to_string()))
        .add_value_converter(RawTypeConverter::to_boxed(new_value_converter()))
        .build();
    let mut property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new(1)), property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, i32>()
        .set_key(Box::new("exist".to_string()))
        .add_value_converter(RawTypeConverter::to_boxed(new_value_converter()))
        .build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(None, property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, i32>()
        .set_key(Box::new("not_exist".to_string()))
        .add_value_converter(RawTypeConverter::to_boxed(new_value_converter()))
        .build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(None, property.get_value());
}

#[test]
fn test_get_property_with_filter() {
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist".to_string())).set_value_filter(Box::new(
            DefaultValueFilter::<String>::new(Box::new(|v|
                if Box::new("ok".to_string()) == v { Some(Box::new("ok_new".to_string())) }
                else { None }
        )))).build();
    let mut property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok_new".to_string())), property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist2".to_string())).set_value_filter(Box::new(
            DefaultValueFilter::<String>::new(Box::new(|v|
                if v.len() >= 8 && v.len() <= 32 { Some(v) } else { None }
        )))).build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(None, property.get_value());
}

#[should_panic]
#[test]
fn test_get_property_with_diff_filter_in_similar_config() {
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist".to_string())).set_value_filter(Box::new(
            DefaultValueFilter::<String>::new(Box::new(|v|
                if Box::new("ok".to_string()) == v { Some(Box::new("ok_new".to_string())) }
                else { None }
        )))).build();
    println!("property: {:?}\n", property_config);
    let property = properties.get_property(property_config.as_ref());
    assert_eq!(Some(Box::new("ok_new".to_string())), property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist".to_string())).set_value_filter(Box::new(
            DefaultValueFilter::<String>::new(Box::new(|v|
                if Box::new("ok".to_string()) == v { Some(Box::new("ok_new".to_string())) }
                else { None }
        )))).build();
    println!("property: {:?}\n", property_config);
    properties.get_property(property_config.as_ref());
}

#[test]
fn test_get_property_with_dynamic_source() {
    let (properties, source) = create_simple_dynamic_properties();
    let property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist".to_string())).build();
    let property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok.2".to_string())), property.get_value());

    source.set_property_value("exist".to_string(), Some("okx".to_string()));
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("okx".to_string())), property.get_value());

    source.set_property_value("exist".to_string(), Some("ok.2".to_string()));
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok.2".to_string())), property.get_value());

    let touched = Arc::new(AtomicBool::new(false));
    let touched_clone = touched.clone();
    property.add_change_listener(Arc::new(Box::new(move |e| {
        println!("touched: {:?}", e);
        touched_clone.swap(true, Ordering::Relaxed);
    })));
    property.add_change_listener(Arc::new(Box::new(move |e| {
        println!("\tproperty: {:?},\n\tchangeTime: {:?},\n\tfrom: {:?},\n\tto: {:?}\n",
        e.get_property(), e.get_change_time(), e.get_old_value(), e.get_new_value())
    })));
    source.set_property_value("exist".to_string(), Some("okx".to_string()));
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("okx".to_string())), property.get_value());
    assert!(touched.fetch_and(true, Ordering::Relaxed));
}

#[test]
fn test_get_properties_multiple_source() {
    let (properties, _, source2) = create_complex_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist".to_string())).build();
    let mut property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(None, property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("not-exist2".to_string()))
        .set_default_value(Box::new("default".to_string())).build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("default".to_string())), property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist".to_string()))
        .set_default_value(Box::new("default".to_string())).build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok.2".to_string())), property.get_value());

    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist5".to_string()))
        .set_default_value(Box::new("default".to_string())).build();
    property = properties.get_property(property_config.as_ref());
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok5".to_string())), property.get_value());

    source2.set_property_value("exist5".to_string(), Some("ok5.2".to_string()));
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok5.2".to_string())), property.get_value());

    source2.set_property_value("exist5".to_string(), None);
    println!("property: {:?}\n", property);
    assert_eq!(Some(Box::new("ok5".to_string())), property.get_value());
}

#[test]
fn test_change_listener() {
    let (properties, source) = create_simple_dynamic_properties();
    let change_count = Arc::new(AtomicI32::new(0));
    let change_count_clone = change_count.clone();
    properties.get_manager().add_raw_change_listener(Arc::new(Box::new(move |e| {
        println!("property changed: {:?}", e);
        change_count_clone.fetch_add(1, Ordering::Relaxed);
    })));

    let property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new("exist".to_string())).build();
    let property = properties.get_property(property_config.as_ref());
    let change_count2 = Arc::new(AtomicI32::new(0));
    let change_count2_clone = change_count2.clone();
    property.add_change_listener(Arc::new(Box::new(move |e| {
        println!("property changed: {:?}", e);
        change_count2_clone.fetch_add(1, Ordering::Relaxed);
    })));

    source.set_property_value("exist".to_string(), Some("okx".to_string()));
    assert_eq!(1, change_count.load(Ordering::Relaxed));
    assert_eq!(1, change_count2.load(Ordering::Relaxed));

    source.set_property_value("exist".to_string(), Some("ok.2".to_string()));
    assert_eq!(2, change_count.load(Ordering::Relaxed));
    assert_eq!(2, change_count2.load(Ordering::Relaxed));

    source.set_property_value("exist".to_string(), Some("okx".to_string()));
    assert_eq!(3, change_count.load(Ordering::Relaxed));
    assert_eq!(3, change_count2.load(Ordering::Relaxed));

    // value not change, no change event
    source.set_property_value("exist".to_string(), Some("okx".to_string()));
    assert_eq!(3, change_count.load(Ordering::Relaxed));
    assert_eq!(3, change_count2.load(Ordering::Relaxed));
}

#[test]
fn test_property_config_doc() {
    let mut doc = None;
    let mut key = "not-exist".to_string();
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).build();
    assert_eq!(doc, property_config.get_doc());
    let mut property = properties.get_property(property_config.as_ref());
    assert_eq!(doc, property.get_config().get_doc());

    doc = Some("test-doc");
    key = "not-exist-2".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key.clone())).set_doc(doc.unwrap()).build();
    assert_eq!(doc, property_config.get_doc());
    property = properties.get_property(property_config.as_ref());
    assert_eq!(doc, property.get_config().get_doc());
}

#[should_panic]
#[test]
fn test_property_config_required() {
    let mut required = false;
    let mut key = "not-exist".to_string();
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).build();
    assert_eq!(required, property_config.is_required());
    let property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    let value = properties.get_property_value(property_config.as_ref());
    assert_eq!(None, value);

    required = true;
    key = "not-exist-2".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).set_required(required).build();
    assert_eq!(required, property_config.is_required());
    properties.get_property(property_config.as_ref());
}

#[should_panic]
#[test]
fn test_property_config_required2() {
    let mut required = false;
    let mut key = "not-exist".to_string();
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).build();
    assert_eq!(required, property_config.is_required());
    let property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    let value = properties.get_property_value(property_config.as_ref());
    assert_eq!(None, value);

    required = true;
    key = "not-exist-2".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).set_required(required).build();
    assert_eq!(required, property_config.is_required());
    properties.get_property_value(property_config.as_ref());
}

#[test]
fn test_property_config_required3() {
    let mut required = false;
    let mut key = "not-exist".to_string();
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).build();
    assert_eq!(required, property_config.is_required());
    let mut property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    let mut value = properties.get_property_value(property_config.as_ref());
    assert_eq!(None, value);

    required = false;
    key = "exist".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).set_required(required).build();
    assert_eq!(required, property_config.is_required());
    property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new("ok".to_string())), value);

    required = true;
    key = "exist2".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).set_required(required).build();
    assert_eq!(required, property_config.is_required());
    property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new("ok2".to_string())), value);
}

#[test]
fn test_property_config_required_default() {
    let mut required = false;
    let mut key = "not-exist".to_string();
    let default_value = "default".to_string();
    let (properties, _) = create_simple_properties();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).set_default_value(Box::new(default_value.clone())).build();
    assert_eq!(required, property_config.is_required());
    let mut property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    let mut value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new(default_value.clone())), value);

    required = true;
    key = "not-exist-2".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).set_default_value(Box::new(default_value.clone()))
        .set_required(required).build();
    assert_eq!(required, property_config.is_required());
    property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new(default_value.clone())), value);
}

#[test]
fn test_property_config_required_dynamic() {
    let (properties, source) = create_simple_dynamic_properties();
    let required = false;
    let key = "exist".to_string();
    let property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key.clone())).set_required(required).build();
    assert_eq!(required, property_config.is_required());
    let property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    assert_eq!(Some(Box::new("ok.2".to_string())), property.get_value());
    let mut value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new("ok.2".to_string())), value);
    source.set_property_value(key, None);
    assert_eq!(None, property.get_value());
    value = properties.get_property_value(property_config.as_ref());
    assert_eq!(None, value);
}

#[should_panic]
#[test]
fn test_property_config_required_dynamic2() {
    let (properties, source) = create_simple_dynamic_properties();
    let required = true;
    let key = "exist2".to_string();
    let property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key.clone())).set_required(required).build();
    assert_eq!(required, property_config.is_required());
    let property = properties.get_property(property_config.as_ref());
    assert_eq!(required, property.get_config().is_required());
    assert_eq!(Some(Box::new("ok2.2".to_string())), property.get_value());
    let value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new("ok2.2".to_string())), value);
    source.set_property_value(key, None);
    assert_eq!(Some(Box::new("ok2.2".to_string())), property.get_value());
    properties.get_property_value(property_config.as_ref());
}

#[test]
fn test_property_config_static() {
    let (properties, source) = create_simple_dynamic_properties();

    let mut is_static = false;
    let mut key = "exist".to_string();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key.clone())).set_static(is_static).build();
    assert_eq!(is_static, property_config.is_static());
    let mut property = properties.get_property(property_config.as_ref());
    assert_eq!(is_static, property.get_config().is_static());
    assert_eq!(Some(Box::new("ok.2".to_string())), property.get_value());
    let mut value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new("ok.2".to_string())), value);
    source.set_property_value(key, None);
    assert_eq!(None, property.get_value());
    value = properties.get_property_value(property_config.as_ref());
    assert_eq!(None, value);

    is_static = true;
    key = "exist2".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key.clone())).set_static(is_static).build();
    assert_eq!(is_static, property_config.is_static());
    property = properties.get_property(property_config.as_ref());
    assert_eq!(is_static, property.get_config().is_static());
    assert_eq!(Some(Box::new("ok2.2".to_string())), property.get_value());
    value = properties.get_property_value(property_config.as_ref());
    assert_eq!(Some(Box::new("ok2.2".to_string())), value);
    source.set_property_value(key, None);
    assert_eq!(Some(Box::new("ok2.2".to_string())), property.get_value());
    value = properties.get_property_value(property_config.as_ref());
    assert_eq!(None, value);
}

#[test]
fn test_property_source() {
    let (properties, source) = create_simple_properties();
    let source = Some(ConfigurationSource::to_boxed(source));

    let mut key = "not-exist".to_string();
    let mut property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).build();
    let mut property = properties.get_property(property_config.as_ref());
    assert_eq!(None, property.get_source());

    key = "exist".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key)).build();
    property = properties.get_property(property_config.as_ref());
    assert_eq!(source, property.get_source());

    let (properties, dynamic_source) = create_simple_dynamic_properties();

    key = "not-exist".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key.clone())).build();
    property = properties.get_property(property_config.as_ref());
    assert_eq!(None, property.get_source());
    dynamic_source.set_property_value(key, Some("ok".to_string()));
    assert_eq!(Some(ConfigurationSource::clone_boxed(&dynamic_source)), property.get_source());

    key = "exist".to_string();
    property_config = ConfigurationProperties::new_config_builder::<String, String>()
        .set_key(Box::new(key.clone())).build();
    property = properties.get_property(property_config.as_ref());
    assert_eq!(Some(ConfigurationSource::clone_boxed(&dynamic_source)), property.get_source());
    dynamic_source.set_property_value(key, None);
    assert_eq!(None, property.get_source());
}
