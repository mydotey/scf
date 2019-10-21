use std::sync::{ Arc, RwLock };
use std::fmt;
use std::time::*;

use super::*;

pub type PropertyProvider = FunctionRef<dyn Key, Option<Box<dyn Value>>>;

#[derive(PartialEq, Eq, Debug, Clone)]
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

impl PartialEq for DefaultConfigurationSource {
    fn eq(&self, other: &Self) -> bool {
        self.property_provider.as_ref().reference_equals(other.property_provider.as_ref())
    }
}

impl Eq for DefaultConfigurationSource {

}

impl fmt::Debug for DefaultConfigurationSource {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ config: {:?} }}", self.config)
    }
}

unsafe impl Sync for DefaultConfigurationSource { }
unsafe impl Send for DefaultConfigurationSource { }

impl ConfigurationSource for DefaultConfigurationSource {
    fn get_config(&self) -> &dyn ConfigurationSourceConfig {
        self.config.as_ref().as_ref()
    }

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>> {
        let lock = self.property_provider.read().unwrap();
        match lock(config.get_raw_key().as_ref()) {
            Some(v) => {
                let type_id = v.as_ref().type_id();
                if type_id == config.get_value_type() {
                    return Some(v);
                } else {
                    for value_converter in config.get_value_converters() {
                        if let Ok(v) = value_converter.convert_raw(v.as_ref()) {
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

as_boxed!(impl ConfigurationSource);
}

#[derive(Eq, Debug, Clone)]
pub struct DefaultConfigurationSourceChangeEvent {
    source: Box<dyn ConfigurationSource>,
    change_time: u64
}

impl DefaultConfigurationSourceChangeEvent {
    pub fn new(source: &dyn ConfigurationSource, change_time: u64) -> Self {
        DefaultConfigurationSourceChangeEvent {
            source: ConfigurationSource::clone_boxed(source),
            change_time
        }
    }
}

impl PartialEq for DefaultConfigurationSourceChangeEvent {
    fn eq(&self, other: &Self) -> bool {
        self.source.eq(&other.source) && self.change_time == other.change_time
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

as_boxed!(impl ConfigurationSourceChangeEvent);
}