use lang_extension::object::*;
use lang_extension::ops::function::*;

use crate::property::*;

pub mod default;

pub trait ConfigurationSourceConfig: Object + Send + Sync {
    fn name(&self) -> &str;
}

pub trait ConfigurationSource : Object + Send + Sync {

    fn get_config(&self) -> &dyn ConfigurationSourceConfig;

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Object>>;

    fn add_change_listener(&self, listener: ConfigurationSourceChangeListener);

    fn clone(&self) -> Box<dyn ConfigurationSource>;

}

pub trait ConfigurationSourceChangeEvent : Object + Send + Sync {

    fn get_source(&self) -> &dyn ConfigurationSource;

    fn get_change_time(&self) -> u64;

}

pub type ConfigurationSourceChangeListener = ConsumerRef<dyn ConfigurationSourceChangeEvent>;
