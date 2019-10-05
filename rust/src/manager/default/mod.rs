use std::sync::{ Arc, RwLock };
use std::collections::HashMap;
use std::fmt;
use std::hash::{ Hash, Hasher };

use super::*;

#[derive(Clone)]
pub struct DefaultConfigurationManager {
    sources: Arc<RwLock<Vec<Box<dyn ConfigurationSource>>>>,
    properties: Arc<RwLock<HashMap<ImmutableObject, Box<dyn RawProperty>>>>
}

impl DefaultConfigurationManager {
    pub fn new(sources: Vec<Box<dyn ConfigurationSource>>) -> DefaultConfigurationManager {
        DefaultConfigurationManager {
            sources: Arc::new(RwLock::new(sources)),
            properties: Arc::new(RwLock::new(HashMap::new()))
        }
    }
}

impl ConfigurationManager for DefaultConfigurationManager {
    fn get_property(&self, config: &dyn RawPropertyConfig) -> Box<dyn RawProperty> {
        let key = ImmutableObject::wrap(config.get_key());
        let mut opt_property = self.properties.read().unwrap().get(&key).map(|p|p.as_ref().clone());
        if opt_property.is_none() {
            let mut map = self.properties.write().unwrap();
            opt_property = match map.get(&key) {
                Some(p) => Some(p.as_ref().clone()),
                None => {
                    let property = DefaultRawProperty::new(config);
                    let value = self.get_property_value(config);
                    property.set_value(value);
                    map.insert(key.clone(), RawProperty::clone(&property));
                    Some(Box::new(property))
                }
            };
        }

        opt_property.unwrap()
    }

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Object>> {
        let lock = self.sources.read().unwrap();
        for source in lock.iter() {
            let value = source.get_property_value(config);
            if value.is_some() {
                return value;
            }
        }

        None
    }

    fn clone(&self) -> Box<dyn ConfigurationManager> {
        Box::new(Clone::clone(self))
    }

}

impl Hash for DefaultConfigurationManager {
    fn hash<H: Hasher>(&self, state: &mut H) {
        let x = self.properties.as_ref() as *const _ as u64;
        state.write_u64(x);
    }
}

impl PartialEq for DefaultConfigurationManager {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.properties.as_ref() as *const _;
        let other_addr = other.properties.as_ref() as *const _;
        addr == other_addr
    }
}

impl Eq for DefaultConfigurationManager {

}

impl fmt::Debug for DefaultConfigurationManager {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ properties: {:?} }}", self.properties.read().unwrap().len())
    }
}

unsafe impl Sync for DefaultConfigurationManager { }
unsafe impl Send for DefaultConfigurationManager { }

#[cfg(test)]
mod test {
    use super::*;
    use std::thread;
    use lang_extension::option::*;
    use crate::source::default::*;

    #[test]
    fn test() {
        let source = DefaultConfigurationSource::new(Box::new(move |o| -> Option<Box<dyn Object>> {
            let key: Box<dyn Object> = Box::new("key");
            if key.as_ref().equals(o) {
                Some(Box::new("ok"))
            } else {
                None
            }
        }));
        let manager = DefaultConfigurationManager::new(vec!(Box::new(source)));
        let config = DefaultPropertyConfig::new("key", Some("default"));
        let property = manager.get_property(&config);
        println!("config: {:?}", config);
        println!("property: {:?}", property.to_debug_string());
        println!("value: {:?}", to_debug_string(&property.get_value()));
        let handle = thread::spawn(move || {
            let property = manager.get_property(&config);
            println!("config: {:?}", config);
            println!("property: {:?}", property.to_debug_string());
            println!("value: {:?}", to_debug_string(&property.get_value()));
        });
        handle.join().unwrap();
    }

}
