use std::fmt;
use std::sync::{ Arc, RwLock, RwLockReadGuard };
use std::cell::RefCell;
use std::marker::PhantomData;
use std::hash::{ Hash, Hasher };

use super::*;

#[derive(Hash, PartialEq, Eq, Debug, Clone)]
pub struct DefaultPropertyConfig<K: ObjectConstraits, V: ObjectConstraits> {
    key: ImmutableObject,
    default_value: Option<ImmutableObject>,
    k: PhantomData<K>,
    v: PhantomData<V>
}

impl <K: ObjectConstraits, V: ObjectConstraits> PropertyConfig<K, V> for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> K {
        self.key.downcast_raw::<K>().unwrap()
    }

    fn get_default_value(&self) -> Option<V> {
        match self.default_value.as_ref() {
            Some(v) => v.downcast_raw::<V>(),
            None => None
        }
    }
}

impl <K: ObjectConstraits, V: ObjectConstraits> DefaultPropertyConfig<K, V> {
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

    pub fn from(config: &dyn PropertyConfig<K, V>) -> Self {
        Self::new(config.get_key(), config.get_default_value())
    }
}

unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Sync for DefaultPropertyConfig<K, V> { }
unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Send for DefaultPropertyConfig<K, V> { }

#[derive(Clone)]
pub struct DefaultProperty<K: ObjectConstraits, V: ObjectConstraits> {
    config: Arc<Box<dyn PropertyConfig<K, V>>>,
    value: Arc<RwLock<RefCell<Option<V>>>>,
    change_listeners: Arc<RwLock<Vec<PropertyChangeListener<K, V>>>>
}

impl <K: ObjectConstraits, V: ObjectConstraits> DefaultProperty<K, V> {
    pub fn new(config: &dyn PropertyConfig<K, V>) -> Self {
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

impl <K: ObjectConstraits, V: ObjectConstraits> Property<K, V> for DefaultProperty<K, V> {
    fn get_config(&self) -> &dyn PropertyConfig<K, V> {
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

impl<K: ObjectConstraits, V: ObjectConstraits> Hash for DefaultProperty<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.get_config().hashcode());
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PartialEq for DefaultProperty<K, V> {
    fn eq(&self, other: &Self) -> bool {
        let addr = self as *const Self;
        let other_addr = other as *const Self;
        addr == other_addr
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> Eq for DefaultProperty<K, V> {

}

impl <K: ObjectConstraits, V: ObjectConstraits> fmt::Debug for DefaultProperty<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ config: {:?}, value: {:?} }}", self.get_config().to_debug_string(),
            option::to_debug_string(&self.get_value()))
    }
}

unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Sync for DefaultProperty<K, V> { }
unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Send for DefaultProperty<K, V> { }

#[derive(Clone)]
pub struct DefaultPropertyChangeEvent<K: ObjectConstraits, V: ObjectConstraits> {
    property: Arc<Box<dyn Property<K, V>>>,
    old_value: Option<V>,
    new_value: Option<V>,
    change_time: u64
}

impl <K: ObjectConstraits, V: ObjectConstraits> DefaultPropertyChangeEvent<K, V> {
    pub fn new(property: Arc<Box<dyn Property<K, V>>>, old_value: Option<V>, new_value: Option<V>,
        change_time: u64) -> Self {
        DefaultPropertyChangeEvent {
            property,
            old_value,
            new_value,
            change_time
        }
    }
}

impl <K: ObjectConstraits, V: ObjectConstraits> PropertyChangeEvent<K, V> for DefaultPropertyChangeEvent<K, V> {
    fn get_property(&self) -> &dyn Property<K, V> {
        self.property.as_ref().as_ref()
    }

    fn get_old_value(&self) -> Option<V> {
        self.old_value.clone()
    }

    fn get_new_value(&self) -> Option<V> {
        self.new_value.clone()
    }

    fn get_change_time(&self) -> u64 {
        self.change_time
    }
}

impl <K: ObjectConstraits, V: ObjectConstraits> fmt::Debug for DefaultPropertyChangeEvent<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ property: {:?}, old_value: {:?}, new_value: {:?}, change_time: {:?} }}",
            self.get_property().to_debug_string(), option::to_debug_string(&self.get_old_value()),
            option::to_debug_string(&self.get_new_value()), self.get_change_time())
    }
}

impl <K: ObjectConstraits, V: ObjectConstraits> PartialEq for DefaultPropertyChangeEvent<K, V> {
    fn eq(&self, other: &Self) -> bool {
        let addr = self as *const Self;
        let other_addr = other as *const Self;
        addr == other_addr
    }
}

impl <K: ObjectConstraits, V: ObjectConstraits> Eq for DefaultPropertyChangeEvent<K, V> {

}

impl <K: ObjectConstraits, V: ObjectConstraits> Hash for DefaultPropertyChangeEvent<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.get_property().hashcode());
        self.get_old_value().hash(state);
        self.get_new_value().hash(state);
        self.get_change_time().hash(state);
    }
}

unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Sync for DefaultPropertyChangeEvent<K, V> { }
unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Send for DefaultPropertyChangeEvent<K, V> { }

#[cfg(test)]
mod test {

    use super::*;

    #[test]
    fn test() {
        let config = DefaultPropertyConfig::new(1, Some(2));
        println!("{:?}", config);
    }

}
