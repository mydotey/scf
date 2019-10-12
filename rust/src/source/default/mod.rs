use std::sync::{ Arc, RwLock };
use std::fmt;
use std::hash::{ Hash, Hasher };
use std::time::*;

use super::*;

pub type PropertyProvider = FunctionRef<dyn Object, Option<Box<dyn Object>>>;

#[derive(Hash, PartialEq, Eq, Debug, Clone)]
pub struct DefaultConfiguratonSourceConfig {
    name: String
}

impl ConfigurationSourceConfig for DefaultConfiguratonSourceConfig {
    fn name(&self) -> &str {
        self.name.as_str()
    }
}

pub struct DefaultConfigurationSourceConfigBuilder {
    name: Option<String>
}

impl DefaultConfigurationSourceConfigBuilder {
    pub fn new() -> Self {
        Self {
            name: None
        }
    }
}

impl ConfigurationSourceConfigBuilder for DefaultConfigurationSourceConfigBuilder {
    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationSourceConfigBuilder {
        self.name = Some(name.to_owned());
        self
    }

    fn build(&self) -> Box<dyn ConfigurationSourceConfig> {
        Box::new(DefaultConfiguratonSourceConfig {
            name: self.name.as_ref().unwrap().to_owned()
        })
    }
}

#[derive(Clone)]
pub struct DefaultConfigurationSource {
    config: Arc<Box<dyn ConfigurationSourceConfig>>,
    property_provider: Arc<RwLock<PropertyProvider>>,
    listeners: Arc<RwLock<Vec<ConfigurationSourceChangeListener>>>
}

impl DefaultConfigurationSource {
    pub fn new(config: Box<dyn ConfigurationSourceConfig>, property_provider: PropertyProvider)
        -> DefaultConfigurationSource {
        DefaultConfigurationSource {
            config: Arc::new(config),
            property_provider: Arc::new(RwLock::new(property_provider)),
            listeners: Arc::new(RwLock::new(Vec::new()))
        }
    }

    pub fn raise_change_event(&self) {
        let start = SystemTime::now();
        let since_the_epoch = start.duration_since(UNIX_EPOCH).unwrap();
        let event = DefaultConfigurationSourceChangeEvent::new(self, since_the_epoch.as_millis() as u64);
        let lock = self.listeners.read().unwrap();
        for listener in lock.iter() {
            listener(&event);
        }
    }
}

impl Hash for DefaultConfigurationSource {
    fn hash<H: Hasher>(&self, state: &mut H) {
        let x = self.property_provider.as_ref() as *const _ as u64;
        state.write_u64(x);
    }
}

impl PartialEq for DefaultConfigurationSource {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.property_provider.as_ref() as *const _;
        let other_addr = other.property_provider.as_ref() as *const _;
        addr == other_addr
    }
}

impl Eq for DefaultConfigurationSource {

}

impl fmt::Debug for DefaultConfigurationSource {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ config: {} }}", self.config.to_debug_string())
    }
}

unsafe impl Sync for DefaultConfigurationSource { }
unsafe impl Send for DefaultConfigurationSource { }

impl ConfigurationSource for DefaultConfigurationSource {
    fn get_config(&self) -> &dyn ConfigurationSourceConfig {
        self.config.as_ref().as_ref()
    }

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Object>> {
        let lock = self.property_provider.read().unwrap();
        match lock(config.get_key().as_ref()) {
            Some(v) => {
                let type_id = v.as_ref().as_any().type_id();
                if type_id == config.get_value_type() {
                    return Some(v);
                } else {
                    for value_converter in config.get_value_converters() {
                        if let Ok(v) = value_converter.convert(v.as_ref()) {
                            return Some(v);
                        }
                    }
                }
                None
            },
            None => None
        }
    }

    fn add_change_listener(&self, listener: ConfigurationSourceChangeListener) {
        let mut lock = self.listeners.write().unwrap();
        lock.push(listener);
    }

    fn clone(&self) -> Box<dyn ConfigurationSource> {
        Box::new(Clone::clone(self))
    }
}

pub struct DefaultConfigurationSourceChangeEvent {
    source: Box<dyn ConfigurationSource>,
    change_time: u64
}

impl DefaultConfigurationSourceChangeEvent {
    pub fn new(source: &dyn ConfigurationSource, change_time: u64) -> Self {
        DefaultConfigurationSourceChangeEvent {
            source: source.clone(),
            change_time
        }
    }
}

impl Hash for DefaultConfigurationSourceChangeEvent {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.source.as_ref().hashcode());
        state.write_u64(self.change_time);
    }
}

impl PartialEq for DefaultConfigurationSourceChangeEvent {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.source.as_ref() as *const _;
        let other_addr = other.source.as_ref() as *const _;
        addr == other_addr && self.change_time == other.change_time
    }
}

impl Eq for DefaultConfigurationSourceChangeEvent {

}

impl fmt::Debug for DefaultConfigurationSourceChangeEvent {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ source: {:?}, change_time: {} }}", self.source.to_debug_string(), self.change_time)
    }
}

impl Clone for DefaultConfigurationSourceChangeEvent {
    fn clone(&self) -> Self {
        Self::new(self.source.as_ref(), self.change_time)
    }
}

unsafe impl Sync for DefaultConfigurationSourceChangeEvent { }
unsafe impl Send for DefaultConfigurationSourceChangeEvent { }

impl ConfigurationSourceChangeEvent for DefaultConfigurationSourceChangeEvent {

    fn get_source(&self) -> &dyn ConfigurationSource {
        self.source.as_ref()
    }

    fn get_change_time(&self) -> u64 {
        self.change_time
    }

}