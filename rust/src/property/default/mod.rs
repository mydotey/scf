use std::fmt;
use std::sync::{ Arc, RwLock };
use std::cell::RefCell;
use std::marker::PhantomData;
use std::hash::{ Hash, Hasher };
use std::fmt::{ Debug, Formatter, Result };
use std::any::TypeId;

use lang_extension::any::*;
use super::*;

#[derive(Clone)]
pub struct DefaultRawPropertyConfig {
    key: ImmutableKey,
    value_type: TypeId,
    default_value: Option<ImmutableValue>,
    value_converters: Arc<Vec<Box<dyn RawTypeConverter>>>,
    value_filter: Option<Box<dyn RawValueFilter>>,
    doc: Option<String>,
    is_static: bool,
    is_required: bool
}

impl DefaultRawPropertyConfig {
    pub fn check_valid(&self) {
        if self.default_value.is_none() || self.value_filter.is_none() {
            return;
        }

        let new_value = self.value_filter.as_ref().unwrap()
            .filter_raw(self.default_value.as_ref().unwrap().raw_boxed());
        if new_value.is_none() {
            panic!("None is returned when the value filter filters the default value, \
                you should either fix the default value or fix the value filter!\n\
                Property Config: {:?}", self);
        }
    }
}

impl RawPropertyConfig for DefaultRawPropertyConfig {
    fn get_raw_key(&self) -> Box<dyn Key> {
        self.key.raw_boxed()
    }

    fn get_value_type(&self) -> TypeId {
        self.value_type
    }

    fn get_raw_default_value(&self) -> Option<Box<dyn Value>> {
        self.default_value.as_ref().map(|v|v.raw_boxed())
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.value_converters.as_ref().as_slice()
    }

    fn get_value_filter(&self) -> Option<&dyn RawValueFilter> {
        self.value_filter.as_ref().map(|f|f.as_ref())
    }

    fn get_doc(&self) -> Option<&str> {
        self.doc.as_ref().map(|v|v.as_str())
    }

    fn is_static(&self) -> bool {
        self.is_static
    }

    fn is_required(&self) -> bool {
        self.is_required
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
            && self.value_converters.as_ref() == other.value_converters.as_ref()
            && self.value_filter == other.value_filter && self.doc == other.doc
            && self.is_static == other.is_static && self.is_required == other.is_required
    }
}

impl Eq for DefaultRawPropertyConfig { }

impl Debug for DefaultRawPropertyConfig {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{} {{ key: {:?}, value_type: {:?}, default_value: {:?}, 
            value_converters: {:?}, value_filter: {:?}, doc: {:?}, static: {:?}, required: {:?} }}",
            self.type_name(), self.key, self.value_type, self.default_value, self.value_converters,
            self.value_filter.type_name(), self.doc, self.is_static, self.is_required)
    }
}

unsafe impl Sync for DefaultRawPropertyConfig { }
unsafe impl Send for DefaultRawPropertyConfig { }

#[derive(Clone)]
pub struct DefaultPropertyConfig<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> {
    raw: Arc<Box<dyn RawPropertyConfig>>,
    k: PhantomData<K>,
    v: PhantomData<V>
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> DefaultPropertyConfig<K, V> {
    pub fn from_raw(config: &dyn RawPropertyConfig) -> Self {
        DefaultPropertyConfig {
            raw: Arc::new(RawPropertyConfig::clone_boxed(config)),
            k: PhantomData::<K>,
            v: PhantomData::<V>
        }
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Hash for DefaultPropertyConfig<K, V> {
    fn hash<H: Hasher>(&self, state: &mut H) {
        self.raw.as_ref().hash(state);
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> PartialEq for DefaultPropertyConfig<K, V> {
    fn eq(&self, other: &Self) -> bool {
        self.raw.as_ref() == other.raw.as_ref()
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Eq for DefaultPropertyConfig<K, V> { }

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> fmt::Debug for DefaultPropertyConfig<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{} {{ key: {:?}, value_type: {:?}, default_value: {:?}, 
            value_converters: {:?} , value_filter: {:?}, doc: {:?}, static: {:?}, required: {:?} }}",
            self.type_name(), self.get_raw_key(), self.get_value_type(), self.get_raw_default_value(),
            self.get_value_converters(), self.get_value_filter().type_name(),
            self.get_doc(), self.is_static(), self.is_required())
    }
}

unsafe impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Sync for DefaultPropertyConfig<K, V> { }
unsafe impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Send for DefaultPropertyConfig<K, V> { }

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> RawPropertyConfig
    for DefaultPropertyConfig<K, V>
{
    fn get_raw_key(&self) -> Box<dyn Key> {
        self.raw.get_raw_key()
    }

    fn get_value_type(&self) -> TypeId {
        self.raw.get_value_type()
    }

    fn get_raw_default_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_raw_default_value()
    }

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>] {
        self.raw.get_value_converters()
    }

    fn get_value_filter(&self) -> Option<&dyn RawValueFilter> {
        self.raw.get_value_filter()
    }

    fn get_doc(&self) -> Option<&str> {
        self.raw.get_doc()
    }

    fn is_static(&self) -> bool {
        self.raw.is_static()
    }

    fn is_required(&self) -> bool {
        self.raw.is_required()
    }

as_boxed!(impl RawPropertyConfig);
as_trait!(impl RawPropertyConfig);
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> PropertyConfig<K, V> for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> Box<K> {
        Box::new(self.raw.get_raw_key().as_ref().as_any_ref().downcast_ref::<K>().unwrap().clone())
    }

    fn get_default_value(&self) -> Option<Box<V>> {
        self.raw.get_raw_default_value().map(
            |v|Box::new(v.as_ref().as_any_ref().downcast_ref::<V>().unwrap().clone()))
    }

as_boxed!(impl PropertyConfig<K, V>);
}

pub struct DefaultPropertyConfigBuilder<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> {
    key: Option<Box<K>>,
    value_type: TypeId,
    default_value: Option<Box<V>>,
    value_converters: Vec<Box<dyn RawTypeConverter>>,
    value_filter: Option<Box<dyn ValueFilter<V>>>,
    doc: Option<String>,
    is_static: bool,
    is_required: bool
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> DefaultPropertyConfigBuilder<K, V> {
    pub fn new() -> Self {
        Self {
            key: None,
            value_type: TypeId::of::<V>(),
            default_value: None,
            value_converters: Vec::new(),
            value_filter: None,
            doc: None,
            is_static: false,
            is_required: false
        }
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> PropertyConfigBuilder<K, V>
    for DefaultPropertyConfigBuilder<K, V> {
    fn set_key(&mut self, key: Box<K>) -> &mut dyn PropertyConfigBuilder<K, V> {
        self.key = Some(key);
        self
    }

    fn set_default_value(&mut self, default_value: Box<V>) -> &mut dyn PropertyConfigBuilder<K, V> {
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

    fn set_value_filter(&mut self, value_filter: Box<dyn ValueFilter<V>>)
        -> &mut dyn PropertyConfigBuilder<K, V> {
        self.value_filter = Some(value_filter);
        self
    }

    fn set_doc(&mut self, doc: &str) -> &mut dyn PropertyConfigBuilder<K, V> {
        self.doc = Some(doc.to_string());
        self
    }

    fn set_static(&mut self, is_static: bool) -> &mut dyn PropertyConfigBuilder<K, V> {
        self.is_static = is_static;
        self
    }

    fn set_required(&mut self, is_required: bool) -> &mut dyn PropertyConfigBuilder<K, V> {
        self.is_required = is_required;
        self
    }

    fn build(&self) -> Box<dyn PropertyConfig<K, V>> {
        let raw = DefaultRawPropertyConfig {
            key: ImmutableKey::wrap(self.key.clone().unwrap()),
            value_type: self.value_type,
            default_value: self.default_value.as_ref().map(|v|v.clone()).map(|v|ImmutableValue::wrap(v)),
            value_converters: Arc::new(self.value_converters.clone()),
            value_filter: self.value_filter.as_ref().map(|f|RawValueFilter::clone_boxed(f.as_ref())),
            doc: self.doc.clone(),
            is_static: self.is_static,
            is_required: self.is_required
        };
        raw.check_valid();
        Box::new(DefaultPropertyConfig {
            raw: Arc::new(RawPropertyConfig::clone_boxed(&raw)),
            k: PhantomData::<K>,
            v: PhantomData::<V>
        })
    }
}

#[derive(Clone)]
pub struct DefaultRawProperty {
    config: Arc<Box<dyn RawPropertyConfig>>,
    value: Arc<RwLock<RefCell<Option<ImmutableValue>>>>,
    source: Arc<RwLock<RefCell<Option<Box<dyn ConfigurationSource>>>>>,
    change_listeners: Arc<RwLock<Vec<RawPropertyChangeListener>>>
}

impl DefaultRawProperty {
    pub fn new(config: &dyn RawPropertyConfig) -> Self {
        DefaultRawProperty {
            config: Arc::new(RawPropertyConfig::clone_boxed(config)),
            value: Arc::new(RwLock::new(RefCell::new(None))),
            source: Arc::new(RwLock::new(RefCell::new(None))),
            change_listeners: Arc::new(RwLock::new(Vec::new()))
        }
    }

    pub fn update(&self, value: Option<Box<dyn Value>>, source: Option<Box<dyn ConfigurationSource>>) {
        let value_lock = self.value.write().unwrap();
        value_lock.replace(value.map(|v|ImmutableValue::wrap(v)));
        let source_lock = self.source.write().unwrap();
        source_lock.replace(source);
    }

    pub fn raise_change_event(&self, event: &dyn RawPropertyChangeEvent) {
        let listeners = self.change_listeners.read().unwrap();
        for listener in listeners.iter() {
            listener(event);
        }
    }
}

impl RawProperty for DefaultRawProperty {
    fn get_raw_config(&self) -> &dyn RawPropertyConfig {
        self.config.as_ref().as_ref()
    }

    fn get_raw_value(&self) -> Option<Box<dyn Value>> {
        let cell = self.value.read().unwrap();
        let r = cell.borrow();
        r.as_ref().map(|v|v.raw_boxed())
    }

    fn add_raw_change_listener(&self, listener: RawPropertyChangeListener) {
        self.change_listeners.write().unwrap().push(listener);
    }

    fn get_source(&self) -> Option<Box<dyn ConfigurationSource>> {
        let cell = self.source.read().unwrap();
        let r = cell.borrow();
        r.as_ref().map(|v|v.clone())
    }

as_boxed!(impl RawProperty);
as_trait!(impl RawProperty);
}

impl PartialEq for DefaultRawProperty {
    fn eq(&self, other: &Self) -> bool {
        self.value.as_ref().reference_equals(other.value.as_ref())
    }
}

impl Eq for DefaultRawProperty {

}

impl fmt::Debug for DefaultRawProperty {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{} {{ value: {:?}, source: {:?}, config: {:?} }}", self.type_name(),
            self.get_raw_config(), self.get_raw_value(),
            self.get_source().map(|v|v.get_config().get_name().to_string()))
    }
}

unsafe impl Sync for DefaultRawProperty { }
unsafe impl Send for DefaultRawProperty { }

#[derive(Clone)]
pub struct DefaultProperty<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> {
    config: Arc<Box<dyn PropertyConfig<K, V>>>,
    raw: Arc<Box<dyn RawProperty>>
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> DefaultProperty<K, V> {
    pub fn from_raw(property: &dyn RawProperty) -> Self {
        DefaultProperty {
            config: Arc::new(Box::new(DefaultPropertyConfig::from_raw(property.get_raw_config()))),
            raw: Arc::new(RawProperty::clone_boxed(property)),
        }
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> PartialEq for DefaultProperty<K, V> {
    fn eq(&self, other: &Self) -> bool {
        self.raw.as_ref() == other.raw.as_ref()
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Eq for DefaultProperty<K, V> { }

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> fmt::Debug for DefaultProperty<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{} {{ value: {:?}, source: {:?}, config: {:?} }}", self.type_name(),
            self.get_raw_config(), self.get_raw_value(),
            self.get_source().map(|v|v.get_config().get_name().to_string()))
    }
}

unsafe impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Sync for DefaultProperty<K, V> { }
unsafe impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Send for DefaultProperty<K, V> { }

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> RawProperty for DefaultProperty<K, V> {
    fn get_raw_config(&self) -> &dyn RawPropertyConfig {
        self.raw.get_raw_config()
    }

    fn get_raw_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_raw_value()
    }

    fn add_raw_change_listener(&self, listener: RawPropertyChangeListener) {
        self.raw.add_raw_change_listener(listener);
    }

    fn get_source(&self) -> Option<Box<dyn ConfigurationSource>> {
        self.raw.get_source()
    }

as_boxed!(impl RawProperty);
as_trait!(impl RawProperty);
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Property<K, V> for DefaultProperty<K, V> {
    fn get_config(&self) -> &dyn PropertyConfig<K, V> {
        self.config.as_ref().as_ref()
    }

    fn get_value(&self) -> Option<Box<V>> {
        self.raw.get_raw_value().map(
            |v|Box::new(v.as_ref().as_any_ref().downcast_ref::<V>().unwrap().clone()))
    }

    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>) {
        self.raw.add_raw_change_listener(Arc::new(Box::new(move |e|{
            listener(&DefaultPropertyChangeEvent::from_raw(e));
        })));
    }

as_boxed!(impl Property<K, V>);
}

#[derive(Clone, Debug)]
pub struct DefaultRawPropertyChangeEvent {
    property: Arc<Box<dyn RawProperty>>,
    old_value: Option<ImmutableValue>,
    new_value: Option<ImmutableValue>,
    change_time: u128
}

impl DefaultRawPropertyChangeEvent {
    pub fn new(property: Arc<Box<dyn RawProperty>>, old_value: Option<ImmutableValue>,
        new_value: Option<ImmutableValue>, change_time: u128) -> Self {
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
        self.property.as_ref() == other.property.as_ref() && self.old_value == other.old_value
            && self.new_value == other.new_value && self.change_time == other.change_time
    }
}

impl Eq for DefaultRawPropertyChangeEvent {

}

unsafe impl Sync for DefaultRawPropertyChangeEvent { }
unsafe impl Send for DefaultRawPropertyChangeEvent { }

impl RawPropertyChangeEvent for DefaultRawPropertyChangeEvent {
    fn get_raw_property(&self) -> &dyn RawProperty {
        self.property.as_ref().as_ref()
    }

    fn get_raw_old_value(&self) -> Option<Box<dyn Value>> {
        self.old_value.as_ref().map(|v|v.raw_boxed())
    }

    fn get_raw_new_value(&self) -> Option<Box<dyn Value>> {
        self.new_value.as_ref().map(|v|v.raw_boxed())
    }

    fn get_change_time(&self) -> u128 {
        self.change_time
    }

as_boxed!(impl RawPropertyChangeEvent);
as_trait!(impl RawPropertyChangeEvent);
}

#[derive(Clone)]
pub struct DefaultPropertyChangeEvent<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> {
    property: Arc<Box<dyn Property<K, V>>>,
    raw: Arc<Box<dyn RawPropertyChangeEvent>>
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> DefaultPropertyChangeEvent<K, V> {
    pub fn from_raw(event: &dyn RawPropertyChangeEvent) -> Self {
        let property = DefaultProperty::<K, V>::from_raw(event.get_raw_property());
        DefaultPropertyChangeEvent {
            property: Arc::new(Box::new(property)),
            raw: Arc::new(RawPropertyChangeEvent::clone_boxed(event))
        }
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> PartialEq for DefaultPropertyChangeEvent<K, V> {
    fn eq(&self, other: &Self) -> bool {
        self.raw.as_ref() == other.raw.as_ref()
    }
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Eq for DefaultPropertyChangeEvent<K, V> {

}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> fmt::Debug for DefaultPropertyChangeEvent<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{} {{ property: {:?}, old_value: {:?}, new_value: {:?}, \
            change_time: {:?} }}", self.type_name(), self.get_property(), self.get_old_value(),
            self.get_new_value(), self.get_change_time())
    }
}

unsafe impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Sync for DefaultPropertyChangeEvent<K, V> { }
unsafe impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> Send for DefaultPropertyChangeEvent<K, V> { }

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> RawPropertyChangeEvent for DefaultPropertyChangeEvent<K, V> {
    fn get_raw_property(&self) -> &dyn RawProperty {
        self.raw.get_raw_property()
    }

    fn get_raw_old_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_raw_old_value()
    }

    fn get_raw_new_value(&self) -> Option<Box<dyn Value>> {
        self.raw.get_raw_new_value()
    }

    fn get_change_time(&self) -> u128 {
        self.raw.get_change_time()
    }

as_boxed!(impl RawPropertyChangeEvent);
as_trait!(impl RawPropertyChangeEvent);
}

impl<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> PropertyChangeEvent<K, V>
    for DefaultPropertyChangeEvent<K, V> {
    fn get_property(&self) -> &dyn Property<K, V> {
        self.property.as_ref().as_ref()
    }

    fn get_old_value(&self) -> Option<Box<V>> {
        self.raw.get_raw_old_value().map(
            |v|Box::new(v.as_ref().as_any_ref().downcast_ref::<V>().unwrap().clone()))
    }

    fn get_new_value(&self) -> Option<Box<V>> {
        self.raw.get_raw_new_value().map(
            |v|Box::new(v.as_ref().as_any_ref().downcast_ref::<V>().unwrap().clone()))
    }

as_boxed!(impl PropertyChangeEvent<K, V>);
}

#[cfg(test)]
mod test {

    use super::*;
    use std::any::Any;
    use std::thread::*;
    use std::time::*;

    #[test]
    fn property_config_test() {
        let c = DefaultTypeConverter::<String, i32>::new(
            Box::new(move |v| match v.parse::<i32>() {
                Ok(v) => Ok(Box::new(v)),
                Err(err) => Err(Box::new(err))
            }));
        let f = DefaultValueFilter::new(Box::new(move |v| {
            if *v > 10 { Some(Box::new(*v + 1)) } else if *v > 0 { Some(v) } else { None }
        }));

        let config = DefaultPropertyConfigBuilder::new().set_key(Box::new(1))
            .set_default_value(Box::new(2))
            .add_value_converter(RawTypeConverter::clone_boxed(&c))
            .set_value_filter(Box::new(f.clone()))
            .set_doc("test property").build();
        println!("{:?}", config);
        assert_eq!(1, *config.get_key());
        assert_eq!(2.type_id(), config.get_value_type());
        assert_eq!(Some(Box::new(2)), config.get_default_value());
        assert_eq!(Some("test property"), config.get_doc());
        assert_eq!(false, config.is_static());
        assert_eq!(false, config.is_required());

        assert_eq!(&config, &config.clone());
        let boxed_config = RawPropertyConfig::clone_boxed(config.as_ref());
        assert_eq!(&boxed_config, &boxed_config.clone());

        let s = "1".to_string();
        let s2 = "xx1".to_string();
        let rc = config.get_value_converters().get(0).unwrap().as_ref();
        assert_eq!(Ok(Value::to_boxed(1)), rc.convert_raw(Value::as_trait_ref(&s)));
        assert_ne!(Ok(Value::to_boxed(2)), rc.convert_raw(Value::as_trait_ref(&s)));
        assert!(rc.convert_raw(Value::as_trait_ref(&s2)).is_err());
        assert!(rc.convert_raw(Value::as_trait_ref(&true)).is_err());
        let rf = config.get_value_filter().unwrap();
        assert_eq!(Some(Value::to_boxed(1)), rf.filter_raw(Value::to_boxed(1)));
        assert_eq!(Some(Value::to_boxed(12)), rf.filter_raw(Value::to_boxed(11)));
        assert_eq!(None, rf.filter_raw(Value::to_boxed(0)));
        assert_eq!(None, rf.filter_raw(Value::to_boxed(s2)));

        let config2 = DefaultPropertyConfigBuilder::new().set_key(Box::new(1))
            .set_default_value(Box::new(2))
            .add_value_converter(RawTypeConverter::clone_boxed(&c))
            .set_value_filter(Box::new(f.clone()))
            .set_doc("test property").build();
        assert!(!config.reference_equals(&config2));
        assert!(!config.as_ref().reference_equals(config2.as_ref().as_any_ref()));
        assert!(config.as_ref().equals(config2.as_ref().as_any_ref()));
        assert!(RawPropertyConfig::as_trait_ref(config.as_ref())
            .equals(RawPropertyConfig::as_trait_ref(config2.as_ref()).as_any_ref()));
        assert_eq!(&RawPropertyConfig::clone_boxed(config.as_ref()),
            &RawPropertyConfig::clone_boxed(config2.as_ref()));
    }

    #[should_panic]
    #[test]
    fn invalid_property_config_test() {
        let f = DefaultValueFilter::new(Box::new(move |v| {
            if *v > 10 { Some(Box::new(*v + 1)) } else if *v > 0 { Some(v) } else { None }
        }));
        DefaultPropertyConfigBuilder::new().set_key(Box::new(1))
            .set_default_value(Box::new(0))
            .set_value_filter(Box::new(f.clone()))
            .set_doc("test property").build();
    }

    #[test]
    fn property_test() {
        let c = DefaultTypeConverter::<String, i32>::new(
            Box::new(move |v| match v.parse::<i32>() {
                Ok(v) => Ok(Box::new(v)),
                Err(err) => Err(Box::new(err))
            }));
        let f = DefaultValueFilter::new(Box::new(move |v| {
            if *v > 10 { Some(Box::new(*v + 1)) } else if *v > 0 { Some(v) } else { None }
        }));
        let config = DefaultPropertyConfigBuilder::new().set_key(Box::new(1))
            .set_default_value(Box::new(2))
            .add_value_converter(RawTypeConverter::clone_boxed(&c))
            .set_value_filter(Box::new(f.clone())).build();

        let property = DefaultRawProperty::new(RawPropertyConfig::as_trait_ref(config.as_ref()));
        println!("property: {:?}", property);
        assert!(property.get_raw_config().equals(config.as_ref().as_any_ref()));
        assert_eq!(None, property.get_raw_value());
        property.update(Some(Box::new(0)), None);
        assert_eq!(Some(Value::to_boxed(0)), property.get_raw_value());
        assert_eq!(None, property.get_source());

        assert_eq!(&property, &property.clone());
        let boxed_property = RawProperty::clone_boxed(&property);
        assert_eq!(&boxed_property, &boxed_property.clone());

        let changed = Arc::new(RwLock::new(Box::new(false)));
        let changed_clone = changed.clone();
        property.add_raw_change_listener(Arc::new(Box::new(move |e|{
            println!("property: {:?}, old_value: {:?}, new_value: {:?}, change_time: {:?}",
                e.get_raw_property(), e.get_raw_old_value(), e.get_raw_new_value(), e.get_change_time());
            let mut v = changed_clone.write().unwrap();
            **v = true;
        })));
        property.update(Some(Box::new(1)), None);
        let arc_property = Arc::new(RawProperty::to_boxed(property.clone()));
        let start = SystemTime::now();
        let since_the_epoch = start.duration_since(UNIX_EPOCH).unwrap();
        let event = DefaultRawPropertyChangeEvent::new(arc_property, Some(ImmutableValue::new(0)),
            Some(ImmutableValue::new(0)), since_the_epoch.as_millis());
        assert_eq!(false, **changed.read().unwrap());
        property.raise_change_event(&event);
        sleep(Duration::from_millis(100));
        assert_eq!(true, **changed.read().unwrap());

        let property2 = DefaultProperty::<i32, i32>::from_raw(&property);
        println!("property: {:?}", property2);
        assert_eq!(Some(Box::new(1)), property2.get_value());
        property.update(Some(Box::new(2)), None);
        assert_eq!(Some(Box::new(2)), property2.get_value());
        let changed2 = Arc::new(RwLock::new(Box::new(false)));
        let changed2_clone = changed2.clone();
        property2.add_change_listener(Arc::new(Box::new(move |e|{
            println!("property: {:?}, old_value: {:?}, new_value: {:?}, change_time: {:?}",
                e.get_property(), e.get_old_value(), e.get_new_value(), e.get_change_time());
            let mut v = changed2_clone.write().unwrap();
            **v = true;
        })));

        assert_eq!(&property2, &property2.clone());
        let boxed_property2 = Property::<i32, i32>::clone_boxed(&property2);
        assert_eq!(&boxed_property2, &boxed_property2.clone());

        let arc_property = Arc::new(RawProperty::to_boxed(property.clone()));
        let start = SystemTime::now();
        let since_the_epoch = start.duration_since(UNIX_EPOCH).unwrap();
        let event = DefaultRawPropertyChangeEvent::new(arc_property, Some(ImmutableValue::new(0)),
            Some(ImmutableValue::new(0)), since_the_epoch.as_millis());
        assert_eq!(false, **changed2.read().unwrap());
        property.raise_change_event(&event);
        sleep(Duration::from_millis(100));
        assert_eq!(true, **changed2.read().unwrap());
    }

    #[test]
    fn property_change_event_test() {
        let config = DefaultPropertyConfigBuilder::new().set_key(Box::new(1))
            .set_default_value(Box::new(2)).build();
        let property = DefaultRawProperty::new(RawPropertyConfig::as_trait_ref(config.as_ref()));
        let arc_property = Arc::new(RawProperty::to_boxed(property.clone()));
        let start = SystemTime::now();
        let since_the_epoch = start.duration_since(UNIX_EPOCH).unwrap();
        let event = DefaultRawPropertyChangeEvent::new(arc_property, Some(ImmutableValue::new(0)),
            Some(ImmutableValue::new(1)), since_the_epoch.as_millis());

        assert_eq!(&event, &event.clone());
        let boxed_event = RawPropertyChangeEvent::clone_boxed(&event);
        assert_eq!(&boxed_event, &boxed_event.clone());

        println!("property event: {:?}", event);
        assert!(property.equals(event.get_raw_property().as_any_ref()));
        assert_eq!(Some(Value::to_boxed(0)), event.get_raw_old_value());
        assert_eq!(Some(Value::to_boxed(1)), event.get_raw_new_value());
        assert_eq!(since_the_epoch.as_millis(), event.get_change_time());
        assert_eq!(event, event.clone());

        let event2 = DefaultPropertyChangeEvent::<i32, i32>::from_raw(&event);
        println!("property event: {:?}", event2);
        assert_eq!(&event2, &event2.clone());
        let boxed_event2 = RawPropertyChangeEvent::clone_boxed(&event2);
        assert_eq!(&boxed_event2, &boxed_event2.clone());
        assert_eq!(1, *event2.get_property().get_config().get_key());
        assert_eq!(Some(Value::to_boxed(0)), event2.get_raw_old_value());
        assert_eq!(Some(Value::to_boxed(1)), event2.get_raw_new_value());
        assert_eq!(Some(Box::new(0)), event2.get_old_value());
        assert_eq!(Some(Box::new(1)), event2.get_new_value());
        assert_eq!(since_the_epoch.as_millis(), event2.get_change_time());
        assert_eq!(event2, event2.clone());
    }

}
