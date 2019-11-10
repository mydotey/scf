use lang_extension::any::*;
use lang_extension::ops::function::*;

use crate::property::*;

pub mod default;

pub trait ConfigurationSourceConfig: Value + Send + Sync {
    /// for description use
    /// 
    /// non-null, non-empty
    fn get_name(&self) -> &str;

as_boxed!(ConfigurationSourceConfig);
}

boxed_value_trait!(ConfigurationSourceConfig);

pub trait ConfigurationSourceConfigBuilder {

    /// required
    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationSourceConfigBuilder;

    fn build(&self) -> Box<dyn ConfigurationSourceConfig>;

}

pub trait ConfigurationSource: Value + Send + Sync {

    fn get_config(&self) -> &dyn ConfigurationSourceConfig;

    /// get property value acccording to the property config
    /// 
    /// if property is configured, the value is of type V
    /// or can be converted to type V by the converters
    /// a value of type V returned
    /// 
    /// otherwise, None returned
    ///
    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>>;

    //// listeners to the source change, notified once source changed
    //// 
    //// will be used by the configuration manager
    fn add_change_listener(&self, listener: ConfigurationSourceChangeListener);

as_boxed!(ConfigurationSource);
}

boxed_value_trait!(ConfigurationSource);

pub trait ConfigurationSourceChangeEvent: Value + Send + Sync {

    fn get_source(&self) -> &dyn ConfigurationSource;

    fn get_change_time(&self) -> u128;

as_boxed!(ConfigurationSourceChangeEvent);
}

boxed_value_trait!(ConfigurationSourceChangeEvent);

pub type ConfigurationSourceChangeListener = ConsumerRef<dyn ConfigurationSourceChangeEvent>;
