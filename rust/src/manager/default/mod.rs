
use crate::value::immutable::*;
use std::sync::RwLock;
use std::collections::HashMap;
use std::sync::Arc;

#[derive(Clone)]
pub struct DefaultConfigurationManager {
    properties: Arc<RwLock<HashMap<Immutable, Immutable>>>
}

impl DefaultConfigurationManager {

    pub fn new() -> DefaultConfigurationManager {
        DefaultConfigurationManager {
            properties: Arc::new(RwLock::new(HashMap::new()))
        }
    }

    pub fn get(&self, key: &Immutable) -> Option<Immutable> {
        let lock = self.properties.read().unwrap();
        match lock.get(key) {
            Some(r) => Some(r.clone()),
            None => None
        }
    }

    pub fn put(&self, key: Immutable, value: Immutable) -> Option<Immutable> {
        let mut map = self.properties.write().unwrap();
        map.insert(key, value)
    }

}

#[cfg(test)]
mod test {
    use super::*;
    use std::thread;

    #[test]
    fn test() {
        let manager = DefaultConfigurationManager::new();
        let key = Immutable::new("key");
        let value = Immutable::new("value");
        let value2 = Immutable::new("value2");
        manager.put(key.clone(), value.clone());
        let v = manager.get(&key);
        assert_eq!(value, v.unwrap());
        let manager2 = manager.clone();
        let key_p = key.clone();
        let value_p = value.clone();
        let value2_p = value2.clone();
        let handle = thread::spawn(move || {
            let v = manager2.get(&key_p);
            assert_eq!(value_p, v.unwrap());
            manager2.put(key_p.clone(), value2_p.clone());
            let v = manager2.get(&key_p);
            assert_eq!(value2_p, v.unwrap());
        });
        handle.join().unwrap();
        let v = manager.get(&key).unwrap();
        assert_eq!(value2, v);
        println!("value = {}", v);
    }
}

/*

impl ConfigurationManager for DefaultConfigurationManager {

    fn get_property<K, V>(&self, config: impl PropertyConfig<K, V>) -> &Property<K, V>
        where
            K: Key,
            V: Value
    {

    }

    fn get_property_value<K, V>(&self, config: impl PropertyConfig<K, V>) -> Option<V>
        where
            K: Key,
            V: Value
    {
        None
    }

}
*/