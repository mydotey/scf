use std::fmt;
use std::sync::{ Arc, RwLock };
use std::cell::RefCell;
use std::marker::PhantomData;
use std::hash::{ Hash, Hasher };
use std::fmt::{ Debug, Formatter, Result };
use std::any::TypeId;

use lang_extension::any::*;
use lang_extension::fmt::*;
use lang_extension::ops::function::*;
use super::*;

#[derive(Clone)]
pub struct DefaultRawPropertyConfig {
    key: ImmutableKey,
    value_type: TypeId,
    default_value: Option<ImmutableValue>,
    value_converters: Arc<Vec<Box<dyn RawTypeConverter>>>,
    value_filter: Option<Arc<Function<Box<dyn Value>, Option<Box<dyn Value>>>>>
}

impl RawPropertyConfig for DefaultRawPropertyConfig {
    fn get_key(&self) -> Box<dyn Key> {
        self.key.raw_boxed()
    }

    fn get_value_type(&self) -> TypeId {
        self.value_type
    }

    fn get_default_value(&self) -> Option<Box<dyn Value>> {
        self.default_value.as_ref().map(|v|v.raw_boxed())
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.value_converters.as_ref().as_slice()
    }

    fn get_value_filter(&self) -> Option<&dyn Fn(Box<dyn Value>) -> Option<Box<dyn Value>>> {
        self.value_filter.as_ref().map(|v|v.as_ref().as_ref())
    }

as_boxed!(impl RawPropertyConfig);
as_trait!(impl RawPropertyConfig);
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

#[derive(Clone, Debug)]
pub struct DefaultPropertyConfig<K: KeyConstraint, V: KeyConstraint> {
    raw: Arc<Box<dyn RawPropertyConfig>>,
    raw_value: Arc<ImmutableKey>,
    k: PhantomData<K>,
    v: PhantomData<V>
}

impl<K: KeyConstraint, V: KeyConstraint> DefaultPropertyConfig<K, V> {
    pub fn from_raw(config: &dyn RawPropertyConfig) -> Self {
        DefaultPropertyConfig {
            raw: Arc::new(RawPropertyConfig::clone_boxed(config)),
            raw_value: Arc::new(ImmutableKey::wrap(Key::clone_boxed(config))),
            k: PhantomData::<K>,
            v: PhantomData::<V>
        }
    }
}

impl<K: KeyConstraint, V: KeyConstraint> Hash for DefaultPropertyConfig<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.raw_value.hash(state);
    }
}

impl<K: KeyConstraint, V: KeyConstraint> PartialEq for DefaultPropertyConfig<K, V> {
    fn eq(&self, other: &Self) -> bool {
        *self.raw_value.as_ref() == *other.raw_value.as_ref()
    }
}

impl<K: KeyConstraint, V: KeyConstraint> Eq for DefaultPropertyConfig<K, V> { }

unsafe impl<K: KeyConstraint, V: KeyConstraint> Sync for DefaultPropertyConfig<K, V> { }
unsafe impl<K: KeyConstraint, V: KeyConstraint> Send for DefaultPropertyConfig<K, V> { }

impl<K: KeyConstraint, V: KeyConstraint> RawPropertyConfig for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> Box<dyn Key> {
        self.raw.get_key()
    }

    fn get_value_type(&self) -> TypeId {
        self.raw.get_value_type()
    }

    fn get_default_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_default_value()
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.raw.get_value_converters()
    }

    fn get_value_filter(&self) -> Option<&dyn Fn(Box<dyn Value>) -> Option<Box<dyn Value>>> {
        self.raw.get_value_filter()
    }

as_boxed!(impl RawPropertyConfig);
as_trait!(impl RawPropertyConfig);
}

impl<K: KeyConstraint, V: KeyConstraint> PropertyConfig<K, V> for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> K {
        self.raw.get_key().as_any_ref().downcast_ref::<K>().unwrap().clone()
    }

    fn get_value_type(&self) -> TypeId {
        self.raw.get_value_type()
    }

    fn get_default_value(&self) -> Option<V> {
        self.raw.as_ref().get_default_value().map(
            |v|v.as_any_ref().downcast_ref::<V>().unwrap().clone())
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.raw.get_value_converters()
    }

    fn get_value_filter(&self) -> Option<&dyn Fn(Box<dyn Value>) -> Option<Box<dyn Value>>> {
        self.raw.get_value_filter()
    }
}

pub struct DefaultPropertyConfigBuilder<K: KeyConstraint, V: KeyConstraint> {
    key: Option<K>,
    value_type: TypeId,
    default_value: Option<V>,
    value_converters: Vec<Box<dyn RawTypeConverter>>,
    value_filter: Option<Arc<Box<dyn Fn(V) -> Option<V>>>>
}

impl<K: KeyConstraint, V: KeyConstraint> DefaultPropertyConfigBuilder<K, V> {
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

impl<K: KeyConstraint, V: KeyConstraint> PropertyConfigBuilder<K, V>
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
            key: ImmutableKey::new(self.key.clone().unwrap()),
            value_type: self.value_type,
            default_value: self.default_value.as_ref().map(|v|v.clone()).map(|v|ImmutableValue::new(v)),
            value_converters: Arc::new(self.value_converters.clone()),
            value_filter: match self.value_filter.as_ref() {
                Some(f) => {
                    let filter = f.clone();
                    Some(Arc::new(Box::new(move |v|{
                        match v.as_any_ref().downcast_ref::<V>() {
                            Some(v) => filter(v.clone()).map(|v|Value::to_boxed(v)),
                            None => None
                        }
                    })))
                },
                None => None
            }
        };
        Box::new(DefaultPropertyConfig {
            raw: Arc::new(RawPropertyConfig::clone_boxed(&raw)),
            raw_value: Arc::new(ImmutableKey::new(raw)),
            k: PhantomData::<K>,
            v: PhantomData::<V>
        })
    }
}

#[derive(Clone)]
pub struct DefaultRawProperty {
    config: Arc<Box<dyn RawPropertyConfig>>,
    value: Arc<RwLock<RefCell<Option<ImmutableValue>>>>,
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

    pub fn set_value(&self, value: Option<Box<dyn Value>>) {
        self.value.write().unwrap().replace(value.map(|v|ImmutableValue::wrap(v)));
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

    fn get_value(&self) -> Option<Box<dyn Value>> {
        let cell = self.value.read().unwrap();
        let r = cell.borrow();
        match r.as_ref() {
            Some(value) => Some(value.raw_boxed()),
            None => RawPropertyConfig::get_default_value(self.get_config())
        }
    }

    fn add_change_listener(&self, listener: RawPropertyChangeListener) {
        let mut listeners = self.change_listeners.write().unwrap();
        listeners.push(listener);
    }

as_boxed!(impl RawProperty);
as_trait!(impl RawProperty);
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
        write!(f, "{{ config: {:?}, value: {} }}", RawProperty::get_config(self),
            RawProperty::get_value(self).to_debug_string())
    }
}

unsafe impl Sync for DefaultRawProperty { }
unsafe impl Send for DefaultRawProperty { }

#[derive(Clone, Debug)]
pub struct DefaultProperty<K: KeyConstraint, V: KeyConstraint> {
    config: Arc<Box<dyn PropertyConfig<K, V>>>,
    raw: Arc<Box<dyn RawProperty>>,
    raw_value: Arc<ImmutableValue>
}

impl<K: KeyConstraint, V: KeyConstraint> DefaultProperty<K, V> {
    pub fn new(config: &dyn PropertyConfig<K, V>) -> Self {
        let raw = DefaultRawProperty::new(RawPropertyConfig::as_trait_ref(config));
        DefaultProperty {
            config: Arc::new(Box::new(DefaultPropertyConfig::<K, V>::from_raw(
                RawPropertyConfig::as_trait_ref(config)))),
            raw: Arc::new(RawProperty::clone_boxed(&raw)),
            raw_value: Arc::new(ImmutableValue::new(raw))
        }
    }

    pub fn from_raw(property: &dyn RawProperty) -> Self {
        DefaultProperty {
            config: Arc::new(Box::new(DefaultPropertyConfig::from_raw(property.get_config()))),
            raw: Arc::new(RawProperty::clone_boxed(property)),
            raw_value: Arc::new(ImmutableValue::wrap(Value::clone_boxed(property)))
        }
    }
}

impl<K: KeyConstraint, V: KeyConstraint> PartialEq for DefaultProperty<K, V> {
    fn eq(&self, other: &Self) -> bool {
        *self.raw_value.as_ref() == *other.raw_value.as_ref()
    }
}

impl<K: KeyConstraint, V: KeyConstraint> Eq for DefaultProperty<K, V> { }

unsafe impl<K: KeyConstraint, V: KeyConstraint> Sync for DefaultProperty<K, V> { }
unsafe impl<K: KeyConstraint, V: KeyConstraint> Send for DefaultProperty<K, V> { }

impl<K: KeyConstraint, V: KeyConstraint> RawProperty for DefaultProperty<K, V> {
    fn get_config(&self) -> &dyn RawPropertyConfig {
        self.raw.get_config()
    }

    fn get_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_value()
    }

    fn add_change_listener(&self, listener: RawPropertyChangeListener) {
        self.raw.add_change_listener(listener);
    }

as_boxed!(impl RawProperty);
as_trait!(impl RawProperty);
}

impl<K: KeyConstraint, V: KeyConstraint> Property<K, V> for DefaultProperty<K, V> {
    fn get_config(&self) -> &dyn PropertyConfig<K, V> {
        self.config.as_ref().as_ref()
    }

    fn get_value(&self) -> Option<V> {
        self.raw.get_value().map(|v|v.as_any_ref().downcast_ref::<V>().unwrap().clone())
    }

    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>) {
        self.raw.add_change_listener(Box::new(move |e|{
            listener(&DefaultPropertyChangeEvent::from_raw(e));
        }));
    }
}

#[derive(Clone, Debug)]
pub struct DefaultRawPropertyChangeEvent {
    property: Arc<Box<dyn RawProperty>>,
    old_value: Option<ImmutableValue>,
    new_value: Option<ImmutableValue>,
    change_time: u64
}

impl DefaultRawPropertyChangeEvent {
    pub fn new(property: Arc<Box<dyn RawProperty>>, old_value: Option<ImmutableValue>,
        new_value: Option<ImmutableValue>, change_time: u64) -> Self {
        DefaultRawPropertyChangeEvent {
            property,
            old_value,
            new_value,
            change_time
        }
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

unsafe impl Sync for DefaultRawPropertyChangeEvent { }
unsafe impl Send for DefaultRawPropertyChangeEvent { }

impl RawPropertyChangeEvent for DefaultRawPropertyChangeEvent {
    fn get_property(&self) -> &dyn RawProperty {
        self.property.as_ref().as_ref()
    }

    fn get_old_value(&self) -> Option<Box<dyn Value>> {
        self.old_value.as_ref().map(|v|v.raw_boxed())
    }

    fn get_new_value(&self) -> Option<Box<dyn Value>> {
        self.new_value.as_ref().map(|v|v.raw_boxed())
    }

    fn get_change_time(&self) -> u64 {
        self.change_time
    }

as_boxed!(impl RawPropertyChangeEvent);
as_trait!(impl RawPropertyChangeEvent);
}

#[derive(Clone, Debug)]
pub struct DefaultPropertyChangeEvent<K: KeyConstraint, V: KeyConstraint> {
    property: Arc<Box<dyn Property<K, V>>>,
    raw: Arc<Box<dyn RawPropertyChangeEvent>>,
    raw_value: Arc<ImmutableValue>,
}

impl<K: KeyConstraint, V: KeyConstraint> DefaultPropertyChangeEvent<K, V> {
    pub fn new(property: Arc<Box<dyn RawProperty>>, old_value: Option<ImmutableValue>,
        new_value: Option<ImmutableValue>, change_time: u64) -> Self {
        let event = DefaultRawPropertyChangeEvent::new(property, old_value, new_value, change_time);
        Self::from_raw(&event)
    }

    pub fn from_raw(event: &dyn RawPropertyChangeEvent) -> Self {
        let property = DefaultProperty::<K, V>::from_raw(event.get_property());
        DefaultPropertyChangeEvent {
            property: Arc::new(Box::new(property)),
            raw: Arc::new(RawPropertyChangeEvent::clone_boxed(event)),
            raw_value: Arc::new(ImmutableValue::wrap(Value::clone_boxed(event)))
        }
    }
}

impl<K: KeyConstraint, V: KeyConstraint> PartialEq for DefaultPropertyChangeEvent<K, V> {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.raw.as_ref().as_ref() as *const _;
        let other_addr = other.raw.as_ref().as_ref() as *const _;
        addr == other_addr
    }
}

impl<K: KeyConstraint, V: KeyConstraint> Eq for DefaultPropertyChangeEvent<K, V> {

}

unsafe impl<K: KeyConstraint, V: KeyConstraint> Sync for DefaultPropertyChangeEvent<K, V> { }
unsafe impl<K: KeyConstraint, V: KeyConstraint> Send for DefaultPropertyChangeEvent<K, V> { }

impl<K: KeyConstraint, V: KeyConstraint> RawPropertyChangeEvent for DefaultPropertyChangeEvent<K, V> {
    fn get_property(&self) -> &dyn RawProperty {
        self.raw.get_property()
    }

    fn get_old_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_old_value()
    }

    fn get_new_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_new_value()
    }

    fn get_change_time(&self) -> u64 {
        self.raw.get_change_time()
    }

as_boxed!(impl RawPropertyChangeEvent);
as_trait!(impl RawPropertyChangeEvent);
}

impl<K: KeyConstraint, V: KeyConstraint> PropertyChangeEvent<K, V>
    for DefaultPropertyChangeEvent<K, V> {
    fn get_property(&self) -> &dyn Property<K, V> {
        self.property.as_ref().as_ref()
    }

    fn get_old_value(&self) -> Option<V> {
        self.raw.get_old_value().map(|v|v.as_any_ref().downcast_ref::<V>().unwrap().clone())
    }

    fn get_new_value(&self) -> Option<V> {
        self.raw.get_new_value().map(|v|v.as_any_ref().downcast_ref::<V>().unwrap().clone())
    }

    fn get_change_time(&self) -> u64 {
        self.raw.get_change_time()
    }
}

#[cfg(test)]
mod test {

    use super::*;

    #[test]
    fn test() {
        let config = DefaultPropertyConfigBuilder::new().set_key(1).set_default_value(2).build();
        println!("{:?}", config);
    }

}
