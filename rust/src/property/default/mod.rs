use std::fmt;
use std::sync::{ Arc, RwLock };
use std::cell::RefCell;
use std::marker::PhantomData;
use std::hash::{ Hash, Hasher };
use std::fmt::{ Debug, Formatter, Result };
use std::any::TypeId;

use lang_extension::any::*;
use lang_extension::option::*;
use lang_extension::ops::function::*;
use super::*;

#[derive(Clone)]
pub struct DefaultRawPropertyConfig {
    key: ImmutableObject,
    value_type: TypeId,
    default_value: Option<ImmutableObject>,
    value_converters: Arc<Vec<Box<dyn RawTypeConverter>>>,
    value_filter: Option<Arc<Function<Box<dyn Object>, Option<Box<dyn Object>>>>>
}

impl RawPropertyConfig for DefaultRawPropertyConfig {
    fn get_key(&self) -> Box<dyn Object> {
        self.key.raw_object()
    }

    fn get_value_type(&self) -> TypeId {
        self.value_type
    }

    fn get_default_value(&self) -> Option<Box<dyn Object>> {
        self.default_value.as_ref().map(|v|v.raw_object())
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.value_converters.as_ref().as_slice()
    }

    fn get_value_filter(&self) -> Option<&dyn Fn(Box<dyn Object>) -> Option<Box<dyn Object>>> {
        self.value_filter.as_ref().map(|v|v.as_ref().as_ref())
    }

    fn clone_boxed(&self) -> Box<dyn RawPropertyConfig> {
        Box::new(Clone::clone(self))
    }
}

impl Hash for DefaultRawPropertyConfig {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.key.hash(state);
    }
}

impl PartialEq for DefaultRawPropertyConfig {
    fn eq(&self, other: &Self) -> bool {
        self.key == other.key && self.value_type == other.value_type
            && self.default_value == other.default_value
            && *self.value_converters.as_ref() == *other.value_converters.as_ref()
            && self.value_filter.as_ref().memory_address() == other.value_filter.as_ref().memory_address()
    }
}

impl Eq for DefaultRawPropertyConfig { }

impl Debug for DefaultRawPropertyConfig {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{{ key: {:?}, value_type: {:?}, default_value: {:?}, value_converters: {:?} \
            , value_filter: {:?} }}", self.key, self.value_type, self.default_value, self.value_converters,
            self.value_filter.type_name())
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
            raw: Arc::new(RawPropertyConfig::clone_boxed(config)),
            raw_object: Arc::new(ImmutableObject::wrap(Object::clone_boxed(config))),
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

    fn get_value_type(&self) -> TypeId {
        self.raw.get_value_type()
    }

    fn get_default_value(&self) -> Option<Box<dyn Object>> {
        self.raw.get_default_value()
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.raw.get_value_converters()
    }

    fn get_value_filter(&self) -> Option<&dyn Fn(Box<dyn Object>) -> Option<Box<dyn Object>>> {
        self.raw.get_value_filter()
    }

    fn clone_boxed(&self) -> Box<dyn RawPropertyConfig> {
        RawPropertyConfig::clone_boxed(self.raw.as_ref().as_ref())
    }
}

impl<K: ObjectConstraits, V: ObjectConstraits> PropertyConfig<K, V> for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> K {
        downcast_raw::<K>(self.raw.get_key()).unwrap()
    }

    fn get_value_type(&self) -> TypeId {
        self.raw.get_value_type()
    }

    fn get_default_value(&self) -> Option<V> {
        self.raw.as_ref().get_default_value().map(|v|downcast_raw::<V>(v).unwrap())
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.raw.get_value_converters()
    }

    fn get_value_filter(&self) -> Option<&dyn Fn(Box<dyn Object>) -> Option<Box<dyn Object>>> {
        self.raw.get_value_filter()
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
    value_type: TypeId,
    default_value: Option<V>,
    value_converters: Vec<Box<dyn RawTypeConverter>>,
    value_filter: Option<Arc<Box<dyn Fn(V) -> Option<V>>>>
}

impl<K: ObjectConstraits, V: ObjectConstraits> DefaultPropertyConfigBuilder<K, V> {
    pub fn new() -> Self {
        Self {
            key: None,
            value_type: TypeId::of::<V>(),
            default_value: None,
            value_converters: Vec::new(),
            value_filter: None
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

    fn add_value_converter(&mut self, value_converter: Box<dyn RawTypeConverter>)
        -> &mut dyn PropertyConfigBuilder<K, V> {
        self.value_converters.push(value_converter);
        self
    }

    fn add_value_converters(&mut self, value_converters: Vec<Box<dyn RawTypeConverter>>)
        -> &mut dyn PropertyConfigBuilder<K, V> {
        let mut value_converters = value_converters;
        self.value_converters.append(&mut value_converters);
        self
    }

    fn set_value_filter(&mut self, value_filter: Box<dyn Fn(V) -> Option<V>>) -> &mut dyn PropertyConfigBuilder<K, V> {
        self.value_filter = Some(Arc::new(value_filter));
        self
    }

    fn build(&self) -> Box<dyn PropertyConfig<K, V>> {
        let raw = DefaultRawPropertyConfig {
            key: ImmutableObject::new(self.key.clone().unwrap()),
            value_type: self.value_type,
            default_value: self.default_value.as_ref().map(|v|v.clone()).map(|v|ImmutableObject::new(v)),
            value_converters: Arc::new(self.value_converters.clone()),
            value_filter: match self.value_filter.as_ref() {
                Some(f) => {
                    let filter = f.clone();
                    Some(Arc::new(Box::new(move |v|{
                        match downcast_raw::<V>(v) {
                            Some(v) => filter(v).map(|v|v.clone_boxed()),
                            None => None
                        }
                    })))
                },
                None => None
            }
        };
        Box::new(DefaultPropertyConfig {
            raw: Arc::new(RawPropertyConfig::clone_boxed(&raw)),
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
            config: Arc::new(RawPropertyConfig::clone_boxed(config)),
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

    fn clone_boxed(&self) -> Box<dyn RawProperty> {
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
            raw: Arc::new(RawProperty::clone_boxed(&raw)),
            raw_object: Arc::new(ImmutableObject::new(raw))
        }
    }

    pub fn from_raw(property: &dyn RawProperty) -> Self {
        DefaultProperty {
            config: Arc::new(Box::new(DefaultPropertyConfig::from_raw(property.get_config()))),
            raw: Arc::new(RawProperty::clone_boxed(property)),
            raw_object: Arc::new(ImmutableObject::wrap(Object::clone_boxed(property)))
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

    fn clone_boxed(&self) -> Box<dyn RawProperty> {
        RawProperty::clone_boxed(self.raw.as_ref().as_ref())
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

    fn clone_boxed(&self) -> Box<dyn RawPropertyChangeEvent> {
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
            raw: Arc::new(RawPropertyChangeEvent::clone_boxed(event)),
            raw_object: Arc::new(ImmutableObject::wrap(Object::clone_boxed(event)))
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

    fn clone_boxed(&self) -> Box<dyn RawPropertyChangeEvent> {
        RawPropertyChangeEvent::clone_boxed(self.raw.as_ref().as_ref())
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
