use std::collections::HashMap;

use lang_extension::any::*;
use lang_extension::ops::function::*;

use crate::property::*;
use crate::source::*;

pub mod default;

pub trait ConfigurationManagerConfig: Value + Send + Sync {
    /// for description use
    fn get_name(&self) -> &str;

    /// key for the source priority, value for the source
    ///
    /// the greater the key is, the higher the priority is
    ///
    /// non-null, non-empty
    fn get_sources(&self) -> &Vec<Box<dyn ConfigurationSource>>;

    /// thread pool for property value update and property change listeners
    ///
    /// by default, property value update and property change listeners
    /// will be done in the source change raising thread
    ///
    /// if property count is too large, or the property change listeners are too slow,
    /// it's better to use an async thread pool
    fn get_task_executor(&self) -> &dyn Fn(&Box<dyn Fn()>);

    as_boxed!(ConfigurationManagerConfig);
}

boxed_value_trait!(ConfigurationManagerConfig);

pub trait ConfigurationManagerConfigBuilder {
    /// required
    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationManagerConfigBuilder;

    /// required
    fn add_source(
        &mut self,
        priority: i32,
        source: Box<dyn ConfigurationSource>,
    ) -> &mut dyn ConfigurationManagerConfigBuilder;

    /// required
    fn add_sources(
        &mut self,
        sources: HashMap<i32, Box<dyn ConfigurationSource>>,
    ) -> &mut dyn ConfigurationManagerConfigBuilder;

    /// optional
    fn set_task_executor(
        &mut self,
        task_executor: ConsumerRef<Box<dyn Fn()>>,
    ) -> &mut dyn ConfigurationManagerConfigBuilder;

    fn build(&self) -> Box<dyn ConfigurationManagerConfig>;
}

pub trait ConfigurationManager: Value + Send + Sync {
    fn get_config(&self) -> &dyn ConfigurationManagerConfig;

    /// the properties created by get_property
    fn get_properties(&self) -> Vec<Box<dyn RawProperty>>;

    /// get property value by get_property_value
    /// and return a property with the propertyConfig and value
    ///
    /// once a property is created, it is kept by the manager and will be auto-update after some configuration source changed
    ///
    /// same property config in, same property out, 1 key 1 property
    fn get_property(&self, config: &dyn RawPropertyConfig) -> Box<dyn RawProperty>;

    /// get property value in each configuration source by source priority
    ///
    /// if non-None value is got by a source ConfigurationSource::get_property_value
    /// if PropertyConfig::get_value_filter() is None, return the non-None value,
    /// if PropertyConfig::get_value_filter() is non-None, apply the filter to the non-None value,
    /// if the new value returned by the filter is non-None, return the new value.
    /// otherwise, go to the next lower priority source
    ///
    /// after handling all sources, no non-None value got, return None
    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>>;

    /// listeners to the property change, notified once property changed
    fn add_raw_change_listener(&self, listener: RawPropertyChangeListener);

    as_boxed!(ConfigurationManager);
}

boxed_value_trait!(ConfigurationManager);
