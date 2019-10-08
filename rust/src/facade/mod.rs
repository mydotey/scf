use lang_extension::object::*;
use std::marker::PhantomData;

use crate::property::*;
use crate::property::default::*;
use crate::source::*;
use crate::source::default::*;
use crate::manager::*;
use crate::manager::default::*;

pub struct ConfigurationManagers {
    _placeholder: PhantomData<i32>
}

impl ConfigurationManagers {
    pub fn new_config_builder() -> Box<dyn ConfigurationManagerConfigBuilder> {
        Box::new(DefaultConfigurationManagerConfigBuilder::new())
    }

    pub fn new_manager(config: Box<dyn ConfigurationManagerConfig>) -> Box<dyn ConfigurationManager> {
        Box::new(DefaultConfigurationManager::new(config))
    }
}

pub struct ConfigurationProperties {
    manager: Box<dyn ConfigurationManager>
}

impl ConfigurationProperties {
    pub fn new_config_builder<K: ObjectConstraits, V: ObjectConstraits>()
        -> Box<dyn PropertyConfigBuilder<K, V>> {
        Box::new(DefaultPropertyConfigBuilder::new())
    }

    pub fn new(manager: Box<dyn ConfigurationManager>) -> Self {
        Self {
            manager
        }
    }

    pub fn get_property<K: ObjectConstraits, V: ObjectConstraits>(&self,
        config: &dyn PropertyConfig<K, V>) -> Box<dyn Property<K, V>> {
        let p = self.manager.get_property(config.as_raw());
        Box::new(DefaultProperty::from_raw(p.as_ref()))
    }

    pub fn get_property_value<K: ObjectConstraits, V: ObjectConstraits>(&self,
        config: &dyn PropertyConfig<K, V>) -> Option<V> {
        self.manager.get_property_value(config.as_raw()).map(|v|downcast_raw::<V>(v).unwrap())
    }
}

pub struct ConfigurationSources {
    _placeholder: PhantomData<i32>
}

impl ConfigurationSources {
    pub fn new_config_builder() -> Box<dyn ConfigurationSourceConfigBuilder> {
        Box::new(DefaultConfigurationSourceConfigBuilder::new())
    }
}