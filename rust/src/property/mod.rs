use std::any::TypeId;

use lang_extension::any::*;
use lang_extension::convert::*;
use lang_extension::ops::function::*;

use crate::source::*;

pub mod default;

pub trait RawPropertyConfig: Key + Send + Sync {
    /// a unique key in a configuration manager to identify a unique property,
    fn get_raw_key(&self) -> Box<dyn Key>;

    /// type of the property value,
    fn get_value_type(&self) -> TypeId;

    /// default value of the property
    ///
    /// default to None
    fn get_raw_default_value(&self) -> Option<Box<dyn Value>>;

    /// type converters used to convert values of different types,
    /// for example, some configuration source has string value,
    /// but integer is needed, so it's necessary to provide a string-to-int converter
    ///
    /// default to empty Vec
    fn get_value_converters(&self) -> &[Box<dyn RawTypeConverter>];

    /// a chance for the user to check the value before using a property value provided by a configuration source,
    /// filter input is non-null, if output is null, the value will be ignored,
    /// if output is non-None, output will be used as the property value
    ///
    /// default to None
    fn get_value_filter(&self) -> Option<&dyn RawValueFilter>;

    /// get property description document
    /// default to None
    fn get_doc(&self) -> Option<&str>;

    /// whether the property is static (not dynamically changeable)
    ///
    /// default to false
    fn is_static(&self) -> bool;

    /// whether the property is required (must be configured or have a default value)
    ///
    /// default to false
    fn is_required(&self) -> bool;

    as_boxed!(RawPropertyConfig);
    as_trait!(RawPropertyConfig);
}

boxed_key_trait!(RawPropertyConfig);

pub trait RawProperty: Value + Send + Sync {
    fn get_raw_config(&self) -> &dyn RawPropertyConfig;

    /// property value, if not configured or no valid value, default to defaultValue Of PropertyConfig
    fn get_raw_value(&self) -> Option<Box<dyn Value>>;

    /// which configuration source is actually used
    /// return None if using default value
    fn get_source(&self) -> Option<Box<dyn ConfigurationSource>>;

    /// listeners to the value change, notified once value changed
    fn add_raw_change_listener(&self, listener: RawPropertyChangeListener);

    as_boxed!(RawProperty);
    as_trait!(RawProperty);
}

boxed_value_trait!(RawProperty);

pub trait RawPropertyChangeEvent: Value + Send + Sync {
    fn get_raw_property(&self) -> &dyn RawProperty;

    fn get_raw_old_value(&self) -> Option<Box<dyn Value>>;

    fn get_raw_new_value(&self) -> Option<Box<dyn Value>>;

    fn get_change_time(&self) -> u128;

    as_boxed!(RawPropertyChangeEvent);
    as_trait!(RawPropertyChangeEvent);
}

boxed_value_trait!(RawPropertyChangeEvent);

pub type RawPropertyChangeListener = ConsumerRef<dyn RawPropertyChangeEvent>;

pub trait PropertyConfig<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>:
    RawPropertyConfig
{
    /// a unique key in a configuration manager to identify a unique property,
    fn get_key(&self) -> Box<K>;

    /// default value of the property
    ///
    /// default to None
    fn get_default_value(&self) -> Option<Box<V>>;

    as_boxed!(PropertyConfig<K, V>);
}

boxed_key_trait!(PropertyConfig<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>);

pub trait PropertyConfigBuilder<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint> {
    /// required
    fn set_key(&mut self, key: Box<K>) -> &mut dyn PropertyConfigBuilder<K, V>;

    /// optional
    fn set_default_value(&mut self, default_value: Box<V>) -> &mut dyn PropertyConfigBuilder<K, V>;

    /// optional
    fn add_value_converter(
        &mut self,
        value_converter: Box<dyn RawTypeConverter>,
    ) -> &mut dyn PropertyConfigBuilder<K, V>;

    /// optional
    fn add_value_converters(
        &mut self,
        value_converters: Vec<Box<dyn RawTypeConverter>>,
    ) -> &mut dyn PropertyConfigBuilder<K, V>;

    /// optional
    fn set_value_filter(
        &mut self,
        value_filter: Box<dyn ValueFilter<V>>,
    ) -> &mut dyn PropertyConfigBuilder<K, V>;

    /// optional
    fn set_doc(&mut self, doc: &str) -> &mut dyn PropertyConfigBuilder<K, V>;

    /// optional
    fn set_static(&mut self, is_static: bool) -> &mut dyn PropertyConfigBuilder<K, V>;

    /// optional
    fn set_required(&mut self, required: bool) -> &mut dyn PropertyConfigBuilder<K, V>;

    fn build(&self) -> Box<dyn PropertyConfig<K, V>>;
}

pub trait Property<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>: RawProperty {
    fn get_config(&self) -> &dyn PropertyConfig<K, V>;

    /// property value, if not configured or no valid value, default to defaultValue Of PropertyConfig
    fn get_value(&self) -> Option<Box<V>>;

    /// listeners to the value change, notified once value changed
    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>);

    as_boxed!(Property<K, V>);
}

boxed_value_trait!(Property<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>);

pub trait PropertyChangeEvent<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>:
    RawPropertyChangeEvent
{
    fn get_property(&self) -> &dyn Property<K, V>;

    fn get_old_value(&self) -> Option<Box<V>>;

    fn get_new_value(&self) -> Option<Box<V>>;

    as_boxed!(PropertyChangeEvent<K, V>);
}

boxed_value_trait!(PropertyChangeEvent<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>);

pub type PropertyChangeListener<K, V> = ConsumerRef<dyn PropertyChangeEvent<K, V>>;
