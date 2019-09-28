use std::sync::{ Arc, RwLock };
use std::collections::HashMap;

use crate::value::*;
use crate::value::immutable::*;
use crate::property::*;
use crate::property::default::*;

#[derive(Clone)]
pub struct ConfigurationManager {
    properties: Arc<RwLock<HashMap<ImmutableObject, ImmutableObject>>>
}

impl ConfigurationManager {

    pub fn new() -> ConfigurationManager {
        ConfigurationManager {
            properties: Arc::new(RwLock::new(HashMap::new()))
        }
    }

    pub fn get_property<K: Object, V: Object>(&self, config: &PropertyConfig<K, V>)
        -> Box<Property<K, V>> {
        let key = ImmutableObject::new(config.get_key());
        let mut property = {
            let map = self.properties.read().unwrap();
            match map.get(&key) {
                Some(r) => Some(r.value()),
                None => None
            }
        };
        if property.is_none() {
            let mut map = self.properties.write().unwrap();
            let mut ref_property = map.get(&key);
            if ref_property.is_none() {
                let property = DefaultProperty::new(config);
                let value = self.get_property_value(config);
                property.set_value(value);
                map.insert(key.clone(), ImmutableObject::new(property));
                ref_property = map.get(&key);
            }

            property = Some(ref_property.unwrap().value());
        }

        property.unwrap().downcast::<DefaultProperty<K, V>>().unwrap()
    }

    pub fn get_property_value<K: Object, V: Object>(&self, config: &PropertyConfig<K, V>)
        -> Option<V> {
        None
    }

}

#[cfg(test)]
mod test {
    use super::*;
    use std::thread;

    #[test]
    fn test() {
        let manager = ConfigurationManager::new();
        let config = DefaultPropertyConfig::new("key", Some("default"));
        let property = manager.get_property(&config);
        println!("config: {}", config);
        println!("property: {}", property);
        println!("value: {}", option::to_string(&property.get_value()));
        let handle = thread::spawn(move || {
            let property = manager.get_property(&config);
            println!("config: {}", config);
            println!("property: {}", property);
            println!("value: {}", option::to_string(&property.get_value()));
        });
        handle.join().unwrap();
    }

}
