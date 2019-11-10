
#[macro_use]
extern crate lang_extension;

use std::collections::HashMap;

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

#[should_panic]
#[test]
fn test_duplicate_priority_source() {
    let source1 = Box::new(create_source());
    let source2 = Box::new(create_dynamic_source());
    ConfigurationManagers::new_config_builder().add_source(1, source1).add_source(1, source2);
}

#[test]
fn test_get_properties() {
    let mut sources = HashMap::new();
    sources.insert(1, ConfigurationSource::to_boxed(create_source()));
    let properties = create_properties(sources);
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
    let mut sources = HashMap::new();
    sources.insert(1, ConfigurationSource::to_boxed(create_source()));
    let properties = create_properties(sources);
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
 