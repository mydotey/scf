use lang_extension::any::*;
use lang_extension::ops::function::*;

use crate::property::*;

pub mod default;

pub trait ConfigurationSourceConfig: Value + Send + Sync {
    fn name(&self) -> &str;
}

pub trait ConfigurationSourceConfigBuilder {

    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationSourceConfigBuilder;

    fn build(&self) -> Box<dyn ConfigurationSourceConfig>;

}

pub trait ConfigurationSource: Value + Send + Sync {

    fn get_config(&self) -> &dyn ConfigurationSourceConfig;

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>>;

    fn add_change_listener(&self, listener: ConfigurationSourceChangeListener);

as_boxed!(ConfigurationSource);

}

pub trait ConfigurationSourceChangeEvent: Value + Send + Sync {

    fn get_source(&self) -> &dyn ConfigurationSource;

    fn get_change_time(&self) -> u64;

}

pub type ConfigurationSourceChangeListener = ConsumerRef<dyn ConfigurationSourceChangeEvent>;
