use std::sync::{ Arc, RwLock };
use std::collections::HashMap;

use lang_extension::value::*;
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

    pub fn get_property<K: ObjectConstraits, V: ObjectConstraits>(&self, config: &dyn PropertyConfig<K, V>)
        -> Box<dyn Property<K, V>> {
        let key = ImmutableObject::new(config.get_key());
        let mut opt_property = {
            let map = self.properties.read().unwrap();
            match map.get(&key) {
                Some(r) => Some(r.downcast_raw::<DefaultProperty<K, V>>().unwrap()),
                None => None
            }
        };

        if opt_property.is_none() {
            let mut map = self.properties.write().unwrap();
            opt_property = match map.get(&key) {
                Some(r) => Some(r.downcast_raw::<DefaultProperty<K, V>>().unwrap()),
                None => {
                    let property = DefaultProperty::new(config);
                    let value = self.get_property_value(config);
                    property.set_value(value);
                    map.insert(key.clone(), ImmutableObject::new(property.clone()));
                    Some(property)
                }
            };
        }

        Box::new(opt_property.unwrap())
    }

    pub fn get_property_value<K: ObjectConstraits, V: ObjectConstraits>(&self, config: &dyn PropertyConfig<K, V>)
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
        println!("config: {:?}", config);
        println!("property: {:?}", property.to_debug_string());
        println!("value: {:?}", option::to_string(&property.get_value()));
        let handle = thread::spawn(move || {
            let property = manager.get_property(&config);
            println!("config: {:?}", config);
            println!("property: {:?}", property.to_debug_string());
            println!("value: {:?}", option::to_string(&property.get_value()));
        });
        handle.join().unwrap();
    }

}
