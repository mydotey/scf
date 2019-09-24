use crate::value::*;
use crate::property::*;

pub mod default;

pub trait ConfigurationManager {

    fn get_property<K, V>(&self, config: impl PropertyConfig<K, V>) -> &Property<K, V>
        where
            K: KeyConstraints,
            V: ValueConstraints;

    fn get_property_value<K, V>(&self, config: impl PropertyConfig<K, V>) -> Option<V>
        where
            K: KeyConstraints,
            V: ValueConstraints;

}