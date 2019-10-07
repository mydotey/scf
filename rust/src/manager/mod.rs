use lang_extension::object::*;
use lang_extension::ops::function::*;

use crate::property::*;
use crate::source::*;

pub mod default;

pub trait ConfigurationManagerConfig: Object + Send + Sync {

    fn get_name(&self) -> &str;

    fn get_sources(&self) -> &Vec<Box<dyn ConfigurationSource>>;

    fn get_task_executor(&self) -> &ConsumerRef<Action>;

}

pub trait ConfigurationManagerConfigBuilder {

    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationManagerConfigBuilder;

    fn add_source(&mut self, priority: i32, source: Box<dyn ConfigurationSource>)
        -> &mut dyn ConfigurationManagerConfigBuilder;

    fn set_task_executor(&mut self, task_executor: ConsumerRef<Action>)
        -> &mut dyn ConfigurationManagerConfigBuilder;

    fn build(&self) -> Box<dyn ConfigurationManagerConfig>;

}

pub trait ConfigurationManager : Object + Send + Sync {

    fn get_config(&self) -> &dyn ConfigurationManagerConfig;

    fn get_property(&self, config: &dyn RawPropertyConfig) -> Box<dyn RawProperty>;

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Object>>;

    fn clone(&self) -> Box<dyn ConfigurationManager>;

}

#[cfg(test)]
mod test {

    #[test]
    fn test() {
    }

}
