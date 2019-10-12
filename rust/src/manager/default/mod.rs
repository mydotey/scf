use std::sync::{ Arc, RwLock };
use std::collections::HashMap;
use std::fmt;
use std::hash::{ Hash, Hasher };

use lang_extension::any::*;
use lang_extension::object::*;

use crate::property::default::*;
use super::*;

#[derive(Clone)]
pub struct DefaultConfigurationManagerConfig {
    name: String,
    sources: Arc<Vec<Box<dyn ConfigurationSource>>>,
    task_executor: Arc<ConsumerRef<Action>>
}

impl Hash for DefaultConfigurationManagerConfig {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_usize(self.sources.as_ref().memory_address())
    }
}

impl PartialEq for DefaultConfigurationManagerConfig {
    fn eq(&self, other: &Self) -> bool {
        self.sources.as_ref().reference_equals(other.sources.as_ref())
    }
}

impl Eq for DefaultConfigurationManagerConfig {

}

impl fmt::Debug for DefaultConfigurationManagerConfig {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ name: {}, sources: {}, task_executor: {} }}", self.name,
            self.sources.as_ref().to_instance_string(), self.task_executor.as_ref().to_instance_string())
    }
}

unsafe impl Sync for DefaultConfigurationManagerConfig { }
unsafe impl Send for DefaultConfigurationManagerConfig { }

impl ConfigurationManagerConfig for DefaultConfigurationManagerConfig {
    fn get_name(&self) -> &str {
        self.name.as_str()
    }

    fn get_sources(&self) -> &Vec<Box<dyn ConfigurationSource>> {
        self.sources.as_ref()
    }

    fn get_task_executor(&self) -> &ConsumerRef<Action> {
        &self.task_executor.as_ref()
    }
}

pub struct DefaultConfigurationManagerConfigBuilder {
    name: Option<String>,
    sources: HashMap<i32, Box<dyn ConfigurationSource>>,
    task_executor: Option<Arc<ConsumerRef<Action>>>
}

impl DefaultConfigurationManagerConfigBuilder {
    pub fn new() -> Self {
        Self {
            name: None,
            sources: HashMap::new(),
            task_executor: Some(Arc::new(Box::new(Self::execute)))
        }
    }

    fn execute(action: &Action) {
        action()
    }
}

impl ConfigurationManagerConfigBuilder for DefaultConfigurationManagerConfigBuilder {

    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationManagerConfigBuilder {
        self.name = Some(name.to_owned());
        self
    }

    fn add_source(&mut self, priority: i32, source: Box<dyn ConfigurationSource>)
        -> &mut dyn ConfigurationManagerConfigBuilder {
        self.sources.insert(priority, source);
        self
    }

    fn set_task_executor(&mut self, task_executor: ConsumerRef<Action>)
        -> &mut dyn ConfigurationManagerConfigBuilder {
        self.task_executor = Some(Arc::new(task_executor));
        self
    }

    fn build(&self) -> Box<dyn ConfigurationManagerConfig> {
        Box::new(DefaultConfigurationManagerConfig {
            name: self.name.as_ref().unwrap().to_owned(),
            sources: Arc::new({
                let mut keys: Vec<_> = self.sources.keys().collect::<Vec<&i32>>()
                    .iter().map(|v|**v).collect();
                keys.sort();
                let mut sources = Vec::new();
                for k in keys {
                    sources.push(self.sources.get(&k).unwrap().as_ref().clone());
                }
                sources
            }),
            task_executor: self.task_executor.as_ref().unwrap().clone()
        })
    }
}

#[derive(Clone)]
pub struct DefaultConfigurationManager {
    config: Arc<Box<dyn ConfigurationManagerConfig>>,
    properties: Arc<RwLock<HashMap<ImmutableObject, Box<dyn RawProperty>>>>
}

impl DefaultConfigurationManager {
    pub fn new(config: Box<dyn ConfigurationManagerConfig>) -> DefaultConfigurationManager {
        let manager = DefaultConfigurationManager {
            config: Arc::new(config),
            properties: Arc::new(RwLock::new(HashMap::new()))
        };
        for s in manager.config.get_sources() {
            let clone = Clone::clone(&manager);
            s.add_change_listener(Box::new(move |e|clone.on_source_change(e)));
        }
        manager
    }

    fn on_source_change(&self, event: &dyn ConfigurationSourceChangeEvent) {
        let properties = self.properties.read().unwrap();
        for property in properties.values() {
            let new_value = self.get_property_value(property.get_config());
            let old_value = property.get_value();
            if new_value != old_value {
                let pe = DefaultRawPropertyChangeEvent::new(
                    Arc::new(RawProperty::clone_boxed(property.as_ref())),
                    old_value.map(|v|ImmutableObject::wrap(v.clone_boxed())),
                    new_value.as_ref().map(|v|ImmutableObject::wrap(v.clone_boxed())), event.get_change_time());
                let raw_property = property.as_ref().clone();
                let default_raw_property = Clone::clone(raw_property.as_any().downcast_ref::<DefaultRawProperty>().unwrap());
                default_raw_property.set_value(new_value);
                let action: Action = Box::new(move || default_raw_property.raise_change_event(&pe));
                self.config.get_task_executor()(&action);
            }
        }
    }
}

impl ConfigurationManager for DefaultConfigurationManager {
    fn get_config(&self) -> &dyn ConfigurationManagerConfig {
        self.config.as_ref().as_ref()
    }

    fn get_property(&self, config: &dyn RawPropertyConfig) -> Box<dyn RawProperty> {
        let key = ImmutableObject::wrap(config.get_key());
        let mut opt_property = self.properties.read().unwrap().get(&key)
            .map(|p|RawProperty::clone_boxed(p.as_ref()));
        if opt_property.is_none() {
            let mut map = self.properties.write().unwrap();
            opt_property = match map.get(&key) {
                Some(p) => Some(RawProperty::clone_boxed(p.as_ref())),
                None => {
                    let property = DefaultRawProperty::new(config);
                    let value = self.get_property_value(config);
                    property.set_value(value);
                    map.insert(key.clone(), RawProperty::clone_boxed(&property));
                    Some(Box::new(property))
                }
            };
        }

        opt_property.unwrap()
    }

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Object>> {
        for source in self.config.get_sources().iter() {
            let value = source.get_property_value(config);
            if value.is_some() {
                return value;
            }
        }

        None
    }

    fn clone(&self) -> Box<dyn ConfigurationManager> {
        Box::new(Clone::clone(self))
    }

}

impl Hash for DefaultConfigurationManager {
    fn hash<H: Hasher>(&self, state: &mut H) {
        let x = self.properties.as_ref() as *const _ as u64;
        state.write_u64(x);
    }
}

impl PartialEq for DefaultConfigurationManager {
    fn eq(&self, other: &Self) -> bool {
        let addr = self.properties.as_ref() as *const _;
        let other_addr = other.properties.as_ref() as *const _;
        addr == other_addr
    }
}

impl Eq for DefaultConfigurationManager {

}

impl fmt::Debug for DefaultConfigurationManager {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ config: {:?}, properties: {:?} }}", self.config.as_ref().as_ref().to_debug_string(),
            self.properties.read().unwrap().to_instance_string())
    }
}

unsafe impl Sync for DefaultConfigurationManager { }
unsafe impl Send for DefaultConfigurationManager { }

#[cfg(test)]
mod test {
    use super::*;
    use std::thread;
    use lang_extension::option::*;
    use lang_extension::convert::*;
    use lang_extension::convert::default::*;
    use crate::source::default::*;

    #[test]
    fn manager_test() {
        let source = DefaultConfigurationSource::new(
            DefaultConfigurationSourceConfigBuilder::new().set_name("test").build(),
            Box::new(move |o| -> Option<Box<dyn Object>> {
                let x = if "key_ok".as_object().equals(o.as_any()) {
                    Some("10".as_object().clone_boxed())
                } else if "key_error".as_object().equals(o.as_any()) {
                    Some("error".as_object().clone_boxed())
                } else {
                    None
                };
                println!("raw_value: {:?}", x);
                x
            }));
        let config = DefaultConfigurationManagerConfigBuilder::new()
            .set_name("test").add_source(1, Box::new(source)).build();
        let manager = DefaultConfigurationManager::new(config);
        let value_converter = DefaultTypeConverter::<&str, i32>::new(Box::new(|s|{
            match s.parse::<i32>() {
                Ok(v) => {
                    println!("parse value: {}", v);
                    Ok(v)
                },
                Err(e) => {
                    println!("parse error: {}", e);
                    Err(Box::new(e.to_string()))
                }
            }
        }));
        let config = DefaultPropertyConfigBuilder::<&str, i32>::new().set_key("key_ok")
            .add_value_converter(RawTypeConverter::clone_boxed(&value_converter)).build();
        let property = manager.get_property(config.as_ref().as_raw());
        println!("config: {:?}", config.to_debug_string());
        println!("property: {:?}", property.to_debug_string());
        println!("value: {:?}", to_debug_string(&property.get_value()));

        let config2 = DefaultPropertyConfigBuilder::<&str, i32>::new().set_key("key_error")
            .add_value_converter(RawTypeConverter::clone_boxed(&value_converter)).build();
        let property2 = manager.get_property(config2.as_ref().as_raw());
        println!("config: {:?}", config2.to_debug_string());
        println!("property: {:?}", property2.to_debug_string());
        println!("value: {:?}", to_debug_string(&property2.get_value()));

        let handle = thread::spawn(move || {
            let property = manager.get_property(config.as_ref().as_raw());
            println!("config: {:?}", config.to_debug_string());
            println!("property: {:?}", property.to_debug_string());
            println!("value: {:?}", to_debug_string(&property.get_value()));
        });
        handle.join().unwrap();
    }

}
