use std::fmt;
use std::sync::{ Arc, RwLock };
use std::cell::RefCell;
use std::marker::PhantomData;
use std::hash::{ Hash, Hasher };
use std::fmt::{ Debug, Formatter, Result };

use lang_extension::option::*;
use super::*;

#[derive(Hash, PartialEq, Eq, Debug, Clone)]
pub struct DefaultRawPropertyConfig {
    key: ImmutableObject,
    default_value: Option<ImmutableObject>
}

impl DefaultRawPropertyConfig {
    pub fn new<K: ObjectConstraits, V: ObjectConstraits>(key: K, default_value: Option<V>) -> Self {
        DefaultRawPropertyConfig {
            key: ImmutableObject::new(key),
            default_value: default_value.map(|v|ImmutableObject::new(v))
        }
    }
}

impl RawPropertyConfig for DefaultRawPropertyConfig {
    fn get_key(&self) -> Box<dyn Object> {
        self.key.raw_object()
    }

    fn get_default_value(&self) -> Option<Box<dyn Object>> {
        self.default_value.as_ref().map(|v|v.raw_object())
    }

    fn clone(&self) -> Box<dyn RawPropertyConfig> {
        Box::new(Clone::clone(self))
    }
}

unsafe impl Sync for DefaultRawPropertyConfig { }
unsafe impl Send for DefaultRawPropertyConfig { }

#[derive(Clone)]
pub struct DefaultPropertyConfig<K: ObjectConstraits, V: ObjectConstraits> {
    raw: Arc<Box<dyn RawPropertyConfig>>,
    raw_object: Arc<ImmutableObject>,
    k: PhantomData<K>,
    v: PhantomData<V>
}

impl<K: ObjectConstraits, V: ObjectConstraits> DefaultPropertyConfig<K, V> {
    pub fn from_raw(config: &dyn RawPropertyConfig) -> Self {
        DefaultPropertyConfig {
            raw: Arc::new(RawPropertyConfig::clone(config)),
            raw_object: Arc::new(ImmutableObject::wrap(config.clone_boxed())),
            k: PhantomData::<K>,
            v: PhantomData::<V>
        }
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> Hash for DefaultPropertyConfig<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.raw_object.hash(state);
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PartialEq for DefaultPropertyConfig<K, V> {
    fn eq(&self, other: &Self) -> bool {
        *self.raw_object.as_ref() == *other.raw_object.as_ref()
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> Eq for DefaultPropertyConfig<K, V> { }

impl<K: ObjectConstraits, V: ObjectConstraits> Debug for DefaultPropertyConfig<K, V> {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{:?}", self.raw_object.as_ref())
    }
}

unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Sync for DefaultPropertyConfig<K, V> { }
unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Send for DefaultPropertyConfig<K, V> { }

impl<K: ObjectConstraits, V: ObjectConstraits> RawPropertyConfig for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> Box<dyn Object> {
        self.raw.get_key()
    }

    fn get_default_value(&self) -> Option<Box<dyn Object>> {
        self.raw.get_default_value()
    }

    fn clone(&self) -> Box<dyn RawPropertyConfig> {
        self.raw.as_ref().as_ref().clone()
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PropertyConfig<K, V> for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> K {
        downcast_raw::<K>(self.raw.get_key()).unwrap()
    }

    fn get_default_value(&self) -> Option<V> {
        self.raw.as_ref().get_default_value().map(|v|downcast_raw::<V>(v).unwrap())
    }

    fn as_raw(&self) -> &dyn RawPropertyConfig {
        self.raw.as_ref().as_ref()
    }

    fn clone(&self) -> Box<dyn PropertyConfig<K, V>> {
        Box::new(Clone::clone(self))
    }
}

pub struct DefaultPropertyConfigBuilder<K: ObjectConstraits, V: ObjectConstraits> {
    key: Option<K>,
    default_value: Option<V>
}

impl<K: ObjectConstraits, V: ObjectConstraits> DefaultPropertyConfigBuilder<K, V> {
    pub fn new() -> Self {
        Self {
            key: None,
            default_value: None
        }
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PropertyConfigBuilder<K, V>
    for DefaultPropertyConfigBuilder<K, V> {
    fn set_key(&mut self, key: K) -> &mut dyn PropertyConfigBuilder<K, V> {
        self.key = Some(key);
        self
    }

    fn set_default_value(&mut self, default_value: V) -> &mut dyn PropertyConfigBuilder<K, V> {
        self.default_value = Some(default_value);
        self
    }

    fn build(&self) -> Box<dyn PropertyConfig<K, V>> {
        let raw = DefaultRawPropertyConfig::new(self.key.as_ref().unwrap().clone(),
            self.default_value.as_ref().map(|v|v.clone()));
        Box::new(DefaultPropertyConfig {
            raw: Arc::new(RawPropertyConfig::clone(&raw)),
            raw_object: Arc::new(ImmutableObject::new(raw)),
            k: PhantomData::<K>,
            v: PhantomData::<V>
        })
    }
}

#[derive(Clone)]
pub struct DefaultRawProperty {
    config: Arc<Box<dyn RawPropertyConfig>>,
    value: Arc<RwLock<RefCell<Option<ImmutableObject>>>>,
    change_listeners: Arc<RwLock<Vec<RawPropertyChangeListener>>>
}

impl DefaultRawProperty {
    pub fn new(config: &dyn RawPropertyConfig) -> Self {
        DefaultRawProperty {
            config: Arc::new(config.clone()),
            value: Arc::new(RwLock::new(RefCell::new(None))),
            change_listeners: Arc::new(RwLock::new(Vec::new()))
        }
    }

    pub fn set_value(&self, value: Option<Box<dyn Object>>) {
        self.value.write().unwrap().replace(value.map(|v|ImmutableObject::wrap(v)));
    }

    pub fn raise_change_event(&self, event: &dyn RawPropertyChangeEvent) {
        let listeners = self.change_listeners.read().unwrap();
        for listener in listeners.iter() {
            listener(event);
        }
    }
}

impl RawProperty for DefaultRawProperty {
    fn get_config(&self) -> &dyn RawPropertyConfig {
        self.config.as_ref().as_ref()
    }

    fn get_value(&self) -> Option<Box<dyn Object>> {
        let cell = self.value.read().unwrap();
        let r = cell.borrow();
        match r.as_ref() {
            Some(value) => Some(value.raw_object()),
            None => RawPropertyConfig::get_default_value(self.get_config())
        }
    }

    fn add_change_listener(&self, listener: RawPropertyChangeListener) {
        let mut listeners = self.change_listeners.write().unwrap();
        listeners.push(listener);
    }

    fn clone(&self) -> Box<dyn RawProperty> {
        Box::new(Clone::clone(self))
    }
}

impl Hash for DefaultRawProperty {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.get_config().hashcode())
    }
}

impl PartialEq for DefaultRawProperty {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.config.as_ref().as_ref() as *const _;
        let other_addr = other.config.as_ref().as_ref() as *const _;
        addr == other_addr
    }
}

impl Eq for DefaultRawProperty {

}

impl fmt::Debug for DefaultRawProperty {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ config: {:?}, value: {:?} }}", RawProperty::get_config(self).to_debug_string(),
            to_debug_string(&RawProperty::get_value(self)))
    }
}

unsafe impl Sync for DefaultRawProperty { }
unsafe impl Send for DefaultRawProperty { }

#[derive(Clone)]
pub struct DefaultProperty<K: ObjectConstraits, V: ObjectConstraits> {
    config: Arc<Box<dyn PropertyConfig<K, V>>>,
    raw: Arc<Box<dyn RawProperty>>,
    raw_object: Arc<ImmutableObject>
}

impl<K: ObjectConstraits, V: ObjectConstraits> DefaultProperty<K, V> {
    pub fn new(config: &dyn PropertyConfig<K, V>) -> Self {
        let raw = DefaultRawProperty::new(config.as_raw());
        DefaultProperty {
            config: Arc::new(PropertyConfig::clone(config)),
            raw: Arc::new(RawProperty::clone(&raw)),
            raw_object: Arc::new(ImmutableObject::new(raw))
        }
    }

    pub fn from_raw(property: &dyn RawProperty) -> Self {
        DefaultProperty {
            config: Arc::new(Box::new(DefaultPropertyConfig::from_raw(property.get_config()))),
            raw: Arc::new(property.clone()),
            raw_object: Arc::new(ImmutableObject::wrap(property.clone_boxed()))
        }
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> Hash for DefaultProperty<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.raw_object.as_ref().hash(state);
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PartialEq for DefaultProperty<K, V> {
    fn eq(&self, other: &Self) -> bool {
        *self.raw_object.as_ref() == *other.raw_object.as_ref()
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> Eq for DefaultProperty<K, V> { }

impl<K: ObjectConstraits, V: ObjectConstraits> Debug for DefaultProperty<K, V> {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{:?}", self.raw_object.as_ref())
    }
}

unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Sync for DefaultProperty<K, V> { }
unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Send for DefaultProperty<K, V> { }

impl<K: ObjectConstraits, V: ObjectConstraits> RawProperty for DefaultProperty<K, V> {
    fn get_config(&self) -> &dyn RawPropertyConfig {
        self.raw.get_config()
    }

    fn get_value(&self) -> Option<Box<dyn Object>> {
        self.raw.get_value()
    }

    fn add_change_listener(&self, listener: RawPropertyChangeListener) {
        self.raw.add_change_listener(listener);
    }

    fn clone(&self) -> Box<dyn RawProperty> {
        self.raw.as_ref().as_ref().clone()
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> Property<K, V> for DefaultProperty<K, V> {
    fn get_config(&self) -> &dyn PropertyConfig<K, V> {
        self.config.as_ref().as_ref()
    }

    fn get_value(&self) -> Option<V> {
        self.raw.get_value().map(|v|downcast_raw::<V>(v).unwrap())
    }

    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>) {
        self.raw.add_change_listener(Box::new(move |e|{
            listener(&DefaultPropertyChangeEvent::from_raw(e));
        }));
    }

    fn as_raw(&self) -> &dyn RawProperty {
        self.raw.as_ref().as_ref()
    }

    fn clone(&self) -> Box<dyn Property<K, V>> {
        Box::new(Clone::clone(self))
    }
}

#[derive(Clone)]
pub struct DefaultRawPropertyChangeEvent {
    property: Arc<Box<dyn RawProperty>>,
    old_value: Option<ImmutableObject>,
    new_value: Option<ImmutableObject>,
    change_time: u64
}

impl DefaultRawPropertyChangeEvent {
    pub fn new(property: Arc<Box<dyn RawProperty>>, old_value: Option<ImmutableObject>,
        new_value: Option<ImmutableObject>, change_time: u64) -> Self {
        DefaultRawPropertyChangeEvent {
            property,
            old_value,
            new_value,
            change_time
        }
    }
}

impl fmt::Debug for DefaultRawPropertyChangeEvent {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ property: {:?}, old_value: {:?}, new_value: {:?}, change_time: {:?} }}",
            self.get_property().to_debug_string(),
            to_debug_string(&self.get_old_value()),
            to_debug_string(&self.get_new_value()),
            self.get_change_time())
    }
}

impl PartialEq for DefaultRawPropertyChangeEvent {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.property.as_ref().as_ref() as *const _;
        let other_addr = other.property.as_ref().as_ref() as *const _;
        addr == other_addr && self.old_value == other.old_value && self.new_value == other.new_value
            && self.change_time == other.change_time
    }
}

impl Eq for DefaultRawPropertyChangeEvent {

}

impl Hash for DefaultRawPropertyChangeEvent {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.get_property().hashcode());
        if let Some(v) = self.get_old_value() {
            v.hash(state);
        }
        if let Some(v) = self.get_new_value() {
            v.hash(state);
        }
        self.get_change_time().hash(state);
    }
}

unsafe impl Sync for DefaultRawPropertyChangeEvent { }
unsafe impl Send for DefaultRawPropertyChangeEvent { }

impl RawPropertyChangeEvent for DefaultRawPropertyChangeEvent {
    fn get_property(&self) -> &dyn RawProperty {
        self.property.as_ref().as_ref()
    }

    fn get_old_value(&self) -> Option<Box<dyn Object>> {
        self.old_value.as_ref().map(|v|v.raw_object())
    }

    fn get_new_value(&self) -> Option<Box<dyn Object>> {
        self.new_value.as_ref().map(|v|v.raw_object())
    }

    fn get_change_time(&self) -> u64 {
        self.change_time
    }

    fn clone(&self) -> Box<dyn RawPropertyChangeEvent> {
        Box::new(Clone::clone(self))
    }
}

#[derive(Clone)]
pub struct DefaultPropertyChangeEvent<K: ObjectConstraits, V: ObjectConstraits> {
    property: Arc<Box<dyn Property<K, V>>>,
    raw: Arc<Box<dyn RawPropertyChangeEvent>>,
    raw_object: Arc<ImmutableObject>,
}

impl<K: ObjectConstraits, V: ObjectConstraits> DefaultPropertyChangeEvent<K, V> {
    pub fn new(property: Arc<Box<dyn RawProperty>>, old_value: Option<ImmutableObject>,
        new_value: Option<ImmutableObject>, change_time: u64) -> Self {
        let event = DefaultRawPropertyChangeEvent::new(property, old_value, new_value, change_time);
        Self::from_raw(&event)
    }

    pub fn from_raw(event: &dyn RawPropertyChangeEvent) -> Self {
        let property = DefaultProperty::<K, V>::from_raw(event.get_property());
        DefaultPropertyChangeEvent {
            property: Arc::new(Box::new(property)),
            raw: Arc::new(event.clone()),
            raw_object: Arc::new(ImmutableObject::wrap(event.clone_boxed()))
        }
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> fmt::Debug for DefaultPropertyChangeEvent<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{}", self.raw.as_ref().as_ref().to_debug_string())
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PartialEq for DefaultPropertyChangeEvent<K, V> {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.raw.as_ref().as_ref() as *const _;
        let other_addr = other.raw.as_ref().as_ref() as *const _;
        addr == other_addr
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> Eq for DefaultPropertyChangeEvent<K, V> {

}

impl<K: ObjectConstraits, V: ObjectConstraits> Hash for DefaultPropertyChangeEvent<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.raw.hashcode());
    }
}

unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Sync for DefaultPropertyChangeEvent<K, V> { }
unsafe impl<K: ObjectConstraits, V: ObjectConstraits> Send for DefaultPropertyChangeEvent<K, V> { }

impl<K: ObjectConstraits, V: ObjectConstraits> RawPropertyChangeEvent for DefaultPropertyChangeEvent<K, V> {
    fn get_property(&self) -> &dyn RawProperty {
        self.raw.get_property()
    }

    fn get_old_value(&self) -> Option<Box<dyn Object>> {
        self.raw.get_old_value()
    }

    fn get_new_value(&self) -> Option<Box<dyn Object>> {
        self.raw.get_new_value()
    }

    fn get_change_time(&self) -> u64 {
        self.raw.get_change_time()
    }

    fn clone(&self) -> Box<dyn RawPropertyChangeEvent> {
        self.raw.as_ref().as_ref().clone()
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PropertyChangeEvent<K, V>
    for DefaultPropertyChangeEvent<K, V> {
    fn get_property(&self) -> &dyn Property<K, V> {
        self.property.as_ref().as_ref()
    }

    fn get_old_value(&self) -> Option<V> {
        self.raw.get_old_value().map(|v|downcast_raw::<V>(v).unwrap())
    }

    fn get_new_value(&self) -> Option<V> {
        self.raw.get_new_value().map(|v|downcast_raw::<V>(v).unwrap())
    }

    fn get_change_time(&self) -> u64 {
        self.raw.get_change_time()
    }

    fn clone(&self) -> Box<dyn PropertyChangeEvent<K, V>> {
        Box::new(Clone::clone(self))
    }

    fn as_raw(&self) -> &dyn RawPropertyChangeEvent {
        self.raw.as_ref().as_ref()
    }
}

#[cfg(test)]
mod test {

    use super::*;

    #[test]
    fn test() {
        let config = DefaultPropertyConfigBuilder::new().set_key(1).set_default_value(2).build();
        println!("{:?}", config.to_debug_string());
    }

}
