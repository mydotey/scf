use lang_extension::any::*;
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
    pub fn new_config_builder<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>()
        -> Box<dyn PropertyConfigBuilder<K, V>> {
        Box::new(DefaultPropertyConfigBuilder::new())
    }

    pub fn new(manager: Box<dyn ConfigurationManager>) -> Self {
        Self {
            manager
        }
    }

    pub fn get_property<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>(&self,
        config: &dyn PropertyConfig<K, V>) -> Box<dyn Property<K, V>> {
        let p = self.manager.get_property(RawPropertyConfig::as_trait_ref(config));
        Box::new(DefaultProperty::from_raw(p.as_ref()))
    }

    pub fn get_property_value<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>(&self,
        config: &dyn PropertyConfig<K, V>) -> Option<V> {
        match self.manager.get_property_value(RawPropertyConfig::as_trait_ref(config)) {
            Some(v) => match v.as_ref().as_any_ref().downcast_ref::<V>() {
                    Some(v) => Some(v.clone()),
                    None => None
            },
            None => None
        }
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