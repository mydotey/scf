use lang_extension::object::*;

use crate::property::*;
use crate::property::default::*;
use crate::source::*;
use crate::source::default::*;
use crate::manager::*;
use crate::manager::default::*;

pub fn new_manager_config_builder() -> Box<dyn ConfigurationManagerConfigBuilder> {
    Box::new(DefaultConfigurationManagerConfigBuilder::new())
}

pub fn new_manager(config: Box<dyn ConfigurationManagerConfig>) -> Box<dyn ConfigurationManager> {
    Box::new(DefaultConfigurationManager::new(config))
}

pub fn get_property<K: ObjectConstraits, V: ObjectConstraits>(manager: &Box<dyn ConfigurationManager>,
    config: &dyn PropertyConfig<K, V>) -> Box<dyn Property<K, V>> {
    let p = manager.get_property(config.as_raw());
    Box::new(DefaultProperty::from_raw(p.as_ref()))
}

pub fn get_property_value<K: ObjectConstraits, V: ObjectConstraits>(manager: &Box<dyn ConfigurationManager>,
    config: &dyn PropertyConfig<K, V>) -> Option<V> {
    manager.get_property_value(config.as_raw()).map(|v|downcast_raw::<V>(v).unwrap())
}

