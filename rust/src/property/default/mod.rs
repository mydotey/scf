use std::fmt;
use std::sync::{ Arc, RwLock, RwLockReadGuard };
use std::cell::RefCell;
use std::marker::PhantomData;
use std::hash::{ Hash, Hasher };

use super::*;
use crate::value::immutable::*;
use crate::value::option;

#[derive(Hash, PartialEq, Eq, Clone, Debug)]
pub struct DefaultPropertyConfig<K: Object, V: Object> {
    key: ImmutableObject,
    default_value: Option<ImmutableObject>,
    k: PhantomData<K>,
    v: PhantomData<V>
}

impl <K: Object, V: Object> PropertyConfig<K, V> for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> K {
        *self.key.value().downcast::<K>().unwrap()
    }

    fn get_default_value(&self) -> Option<V> {
        match self.default_value.as_ref() {
            Some(value) => Some(*value.value().downcast::<V>().unwrap()),
            None => None
        }
    }
}

impl <K: Object, V: Object> DefaultPropertyConfig<K, V> {
    pub fn new(key: K, default_value: Option<V>) -> Self {
        DefaultPropertyConfig {
            key: ImmutableObject::new(key),
            default_value: match default_value {
                Some(value) => Some(ImmutableObject::new(value)),
                None => None
            },
            k: PhantomData,
            v: PhantomData
        }
    }

    pub fn from(config: &PropertyConfig<K, V>) -> Self {
        Self::new(config.get_key(), config.get_default_value())
    }
}

impl <K: Object, V: Object> fmt::Display for DefaultPropertyConfig<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ key: {}, default_value: {} }}", self.key, option::to_string(&self.default_value))
    }
}

unsafe impl<K: Object, V: Object> Sync for DefaultPropertyConfig<K, V> { }
unsafe impl<K: Object, V: Object> Send for DefaultPropertyConfig<K, V> { }

#[derive(Clone)]
pub struct DefaultProperty<K: Object, V: Object> {
    config: Arc<Box<PropertyConfig<K, V>>>,
    value: Arc<RwLock<RefCell<Option<V>>>>,
    change_listeners: Arc<RwLock<Vec<PropertyChangeListener<K, V>>>>
}

impl <K: Object, V: Object> DefaultProperty<K, V> {
    pub fn new(config: &PropertyConfig<K, V>) -> Self {
        DefaultProperty {
            config: Arc::new(Box::new(DefaultPropertyConfig::from(config))),
            value: Arc::new(RwLock::new(RefCell::new(None))),
            change_listeners: Arc::new(RwLock::new(Vec::new()))
        }
    }

    pub fn set_value(&self, value: Option<V>) {
        let cell = self.value.write().unwrap();
        cell.replace(value);
    }

    pub fn get_change_listeners(&self) -> RwLockReadGuard<Vec<PropertyChangeListener<K, V>>> {
        self.change_listeners.read().unwrap()
    }
}

impl <K: Object, V: Object> Property<K, V> for DefaultProperty<K, V> {
    fn get_config(&self) -> &PropertyConfig<K, V> {
        self.config.as_ref().as_ref()
    }

    fn get_value(&self) -> Option<V> {
        let cell = self.value.read().unwrap();
        let r = cell.borrow();
        match r.as_ref() {
            Some(value) => Some(value.clone()),
            None => self.config.get_default_value()
        }
    }

    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>) {
        let mut listeners = self.change_listeners.write().unwrap();
        listeners.push(listener);
    }
}

impl<K: Object, V: Object> Hash for DefaultProperty<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        let addr = self as *const Self as u64;
        state.write_u64(addr);
    }
}

impl<K: Object, V: Object> PartialEq for DefaultProperty<K, V> {
    fn eq(&self, other: &Self) -> bool {
        let addr = self as *const Self as u64;
        let other_addr = other as *const Self as u64;
        addr == other_addr
    }
}

impl<K: Object, V: Object> Eq for DefaultProperty<K, V> {

}

impl <K: Object, V: Object> fmt::Display for DefaultProperty<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ config: {}, value: {} }}", self.get_config(),
            option::to_string(&self.get_value()))
    }
}

impl <K: Object, V: Object> fmt::Debug for DefaultProperty<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ config: {}, value: {} }}", self.get_config(),
            option::to_debug_string(&self.get_value()))
    }
}

unsafe impl<K: Object, V: Object> Sync for DefaultProperty<K, V> { }
unsafe impl<K: Object, V: Object> Send for DefaultProperty<K, V> { }

pub struct DefaultPropertyChangeEvent<K: Object, V: Object> {
    property: Arc<Box<Property<K, V>>>,
    old_value: Option<V>,
    new_value: Option<V>,
    change_time: i64
}

impl <K: Object, V: Object> DefaultPropertyChangeEvent<K, V> {
    pub fn new(property: Arc<Box<Property<K, V>>>, old_value: Option<V>, new_value: Option<V>,
        change_time: i64) -> Self {
        DefaultPropertyChangeEvent {
            property,
            old_value,
            new_value,
            change_time
        }
    }
}

impl <K: Object, V: Object> PropertyChangeEvent<K, V> for DefaultPropertyChangeEvent<K, V> {
    fn get_property(&self) -> &Property<K, V> {
        self.property.as_ref().as_ref()
    }

    fn get_old_value(&self) -> Option<V> {
        self.old_value.clone()
    }

    fn get_new_value(&self) -> Option<V> {
        self.new_value.clone()
    }

    fn get_change_time(&self) -> i64 {
        self.change_time
    }
}

impl <K: Object, V: Object> fmt::Display for DefaultPropertyChangeEvent<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ property: {}, old_value: {}, new_value: {}, change_time: {} }}", self.get_property(),
            option::to_string(&self.get_old_value()), option::to_string(&self.get_new_value()),
            &self.get_change_time())
    }
}

impl <K: Object, V: Object> fmt::Debug for DefaultPropertyChangeEvent<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ property: {:?}, old_value: {:?}, new_value: {:?}, change_time: {:?} }}",
            self.get_property(), option::to_string(&self.get_old_value()),
            option::to_string(&self.get_new_value()), self.get_change_time())
    }
}

unsafe impl<K: Object, V: Object> Sync for DefaultPropertyChangeEvent<K, V> { }
unsafe impl<K: Object, V: Object> Send for DefaultPropertyChangeEvent<K, V> { }

#[cfg(test)]
mod test {

    use super::*;

    #[test]
    fn test() {
        let config = DefaultPropertyConfig::new(1, Some(2));
        println!("{}", config);
    }

}
