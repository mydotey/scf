use std::any::TypeId;

use lang_extension::any::*;
use lang_extension::convert::*;

pub mod default;

pub trait RawPropertyConfig: Key + Send + Sync {
    fn get_key(&self) -> Box<dyn Key>;

    fn get_value_type(&self) -> TypeId;

    fn get_default_value(&self) -> Option<Box<dyn Value>>;

    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>];

    fn get_value_filter(&self) -> Option<&dyn Fn(Box<dyn Value>) -> Option<Box<dyn Value>>>;

as_boxed!(RawPropertyConfig);
as_trait!(RawPropertyConfig);
}

boxed_key_trait!(RawPropertyConfig);

pub trait RawProperty: Value + Send + Sync {
    fn get_config(&self) -> &dyn RawPropertyConfig;

    fn get_value(&self) -> Option<Box<dyn Value>>;

    fn add_change_listener(&self, listener: RawPropertyChangeListener);

as_boxed!(RawProperty);
as_trait!(RawProperty);
}

boxed_value_trait!(RawProperty);

pub trait RawPropertyChangeEvent: Value + Send + Sync {
    fn get_property(&self) -> &dyn RawProperty;

    fn get_old_value(&self) -> Option<Box<dyn Value>>;

    fn get_new_value(&self) -> Option<Box<dyn Value>>;

    fn get_change_time(&self) -> u64;

as_boxed!(RawPropertyChangeEvent);
as_trait!(RawPropertyChangeEvent);
}

boxed_value_trait!(RawPropertyChangeEvent);

pub type RawPropertyChangeListener = Box<dyn Fn(&dyn RawPropertyChangeEvent)>;

pub trait PropertyConfig<K: KeyConstraint, V: KeyConstraint> : RawPropertyConfig {
    fn get_key(&self) -> K;

    fn get_default_value(&self) -> Option<V>;
}

pub trait PropertyConfigBuilder<K: KeyConstraint, V: KeyConstraint> {
    fn set_key(&mut self, key: K) -> &mut dyn PropertyConfigBuilder<K, V>;

    fn set_default_value(&mut self, default_value: V) -> &mut dyn PropertyConfigBuilder<K, V>;

    fn add_value_converter(&mut self, value_converter: Box<dyn RawTypeConverter>)
        -> &mut dyn PropertyConfigBuilder<K, V>;

    fn add_value_converters(&mut self, value_converters: Vec<Box<dyn RawTypeConverter>>)
        -> &mut dyn PropertyConfigBuilder<K, V>;

    fn set_value_filter(&mut self, value_filter: Box<dyn Fn(V) -> Option<V>>) -> &mut dyn PropertyConfigBuilder<K, V>;

    fn build(&self) -> Box<dyn PropertyConfig<K, V>>;
}

pub trait Property<K: KeyConstraint, V: KeyConstraint> : RawProperty {
    fn get_config(&self) -> &dyn PropertyConfig<K, V>;

    fn get_value(&self) -> Option<V>;

    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>);
}

pub trait PropertyChangeEvent<K: KeyConstraint, V: KeyConstraint> : RawPropertyChangeEvent {
    fn get_property(&self) -> &dyn Property<K, V>;

    fn get_old_value(&self) -> Option<V>;

    fn get_new_value(&self) -> Option<V>;
}

pub type PropertyChangeListener<K, V> = Box<dyn Fn(&dyn PropertyChangeEvent<K, V>)>;
