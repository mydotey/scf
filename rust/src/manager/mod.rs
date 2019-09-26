use crate::value::*;
use crate::property::*;

pub mod default;

pub trait ConfigurationManager {

    fn get_property<K: Value, V: Value>(&self, config: impl PropertyConfig<K, V>) -> &Property<K, V>;

    fn get_property_value<K: Value, V: Value>(&self, config: impl PropertyConfig<K, V>) -> Option<V>;

}