use std::collections::HashMap;
use std::sync::Arc;
use std::sync::RwLock;
use std::fmt;

use lang_extension::any::*;
use scf_core::source::*;
use scf_core::source::default::*;
use scf_core::property::*;

#[derive(Clone)]
pub struct TestDynamicConfigurationSource {
    source: DefaultConfigurationSource,
    properties: Arc<RwLock<HashMap<String, String>>>
}

impl TestDynamicConfigurationSource {
    pub fn new(config: Box<dyn ConfigurationSourceConfig>, properties: HashMap<String, String>) -> Self {
        let properties = Arc::new(RwLock::new(properties));
        let properties_clone = properties.clone();
        let property_provider: PropertyProvider = Arc::new(Box::new(move |k|{
            match k.as_any_ref().downcast_ref::<String>() {
                Some(k) => properties_clone.read().unwrap().get(k).map(|v|Value::clone_boxed(v)),
                None => None
            }
        }));
        let source = DefaultConfigurationSource::new(config, property_provider);
        Self {
            source,
            properties
        }
    }

    pub fn set_property_value(&self, key: String, value: Option<String>) {
        match value {
            Some(value) => self.properties.write().unwrap().insert(key, value),
            None => self.properties.write().unwrap().remove(&key)
        };

        self.source.raise_change_event()
    }
}

impl PartialEq for TestDynamicConfigurationSource {
    fn eq(&self, other: &Self) -> bool {
        self.source == other.source
    }
}

impl Eq for TestDynamicConfigurationSource {

}

impl fmt::Debug for TestDynamicConfigurationSource {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{} {{ config: {:?} }}", self.type_name(), self.source.get_config())
    }
}

unsafe impl Sync for TestDynamicConfigurationSource { }
unsafe impl Send for TestDynamicConfigurationSource { }

impl ConfigurationSource for TestDynamicConfigurationSource {
    fn get_config(&self) -> &dyn ConfigurationSourceConfig {
        self.source.get_config()
    }

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>> {
        self.source.get_property_value(config)
    }

    fn add_change_listener(&self, listener: ConfigurationSourceChangeListener) {
        self.source.add_change_listener(listener);
    }

as_boxed!(impl ConfigurationSource);
}
