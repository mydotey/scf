use lang_extension::any::*;
use lang_extension::ops::function::*;

use crate::property::*;
use crate::source::*;

pub mod default;

pub trait ConfigurationManagerConfig: Value + Send + Sync {

    fn get_name(&self) -> &str;

    fn get_sources(&self) -> &Vec<Box<dyn ConfigurationSource>>;

    fn get_task_executor(&self) -> &dyn Fn(&Box<dyn Fn()>);

as_boxed!(ConfigurationManagerConfig);
}

boxed_value_trait!(ConfigurationManagerConfig);

pub trait ConfigurationManagerConfigBuilder {

    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationManagerConfigBuilder;

    fn add_source(&mut self, priority: i32, source: Box<dyn ConfigurationSource>)
        -> &mut dyn ConfigurationManagerConfigBuilder;

    fn set_task_executor(&mut self, task_executor: ConsumerRef<Box<dyn Fn()>>)
        -> &mut dyn ConfigurationManagerConfigBuilder;

    fn build(&self) -> Box<dyn ConfigurationManagerConfig>;

}

pub trait ConfigurationManager: Value + Send + Sync {

    fn get_config(&self) -> &dyn ConfigurationManagerConfig;

    fn get_property(&self, config: &dyn RawPropertyConfig) -> Box<dyn RawProperty>;

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>>;

as_boxed!(ConfigurationManager);
}

boxed_value_trait!(ConfigurationManager);

#[cfg(test)]
mod test {

    #[test]
    fn test() {
    }

}
