
use lang_extension::object::*;
use crate::property::*;
use crate::property::default::*;

pub mod default;

pub trait ConfigurationManager : Object + Send + Sync {

    fn get_property(&self, config: &dyn RawPropertyConfig) -> Box<dyn RawProperty>;

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Object>>;

    fn clone(&self) -> Box<dyn ConfigurationManager>;

}

pub fn get_property<K: ObjectConstraits, V: ObjectConstraits>(manager: &dyn ConfigurationManager,
    config: &dyn PropertyConfig<K, V>) -> Box<dyn Property<K, V>> {
    let p = manager.get_property(config.as_raw());
    Box::new(DefaultProperty::from_raw(p.as_ref()))
}

pub fn get_property_value<K: ObjectConstraits, V: ObjectConstraits>(manager: &dyn ConfigurationManager,
    config: &dyn PropertyConfig<K, V>) -> Option<V> {
    manager.get_property_value(config.as_raw()).map(|v|downcast_raw::<V>(v).unwrap())
}

#[cfg(test)]
mod test {

    #[test]
    fn test() {
    }

}
