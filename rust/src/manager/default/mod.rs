use std::sync::{ Arc, RwLock };
use std::collections::HashMap;
use std::fmt;

use lang_extension::any::*;
use lang_extension::fmt::*;

use crate::property::default::*;
use super::*;

#[derive(Clone)]
pub struct DefaultConfigurationManagerConfig {
    name: String,
    sources: Arc<Vec<Box<dyn ConfigurationSource>>>,
    task_executor: ConsumerRef<Box<dyn Fn()>>
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
        write!(f, "{{ name: {}, sources: {:?}, task_executor: {} }}", self.name,
            self.sources, self.task_executor.as_ref().to_instance_string())
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

    fn get_task_executor(&self) -> &dyn Fn(&Box<dyn Fn()>) {
        self.task_executor.as_ref().as_ref()
    }
}

pub struct DefaultConfigurationManagerConfigBuilder {
    name: Option<String>,
    sources: HashMap<i32, Box<dyn ConfigurationSource>>,
    task_executor: Option<ConsumerRef<Box<dyn Fn()>>>
}

impl DefaultConfigurationManagerConfigBuilder {
    pub fn new() -> Self {
        Self {
            name: None,
            sources: HashMap::new(),
            task_executor: Some(arc_boxed!(Self::execute))
        }
    }

    fn execute(action: &Box<dyn Fn()>) {
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

    fn set_task_executor(&mut self, task_executor: ConsumerRef<Box<dyn Fn()>>)
        -> &mut dyn ConfigurationManagerConfigBuilder {
        self.task_executor = Some(task_executor);
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
                    sources.push(ConfigurationSource::clone_boxed(self.sources.get(&k).unwrap().as_ref()));
                }
                sources
            }),
            task_executor: self.task_executor.as_ref().unwrap().clone()
        })
    }
}

#[derive(Clone, Debug)]
pub struct DefaultConfigurationManager {
    config: Arc<Box<dyn ConfigurationManagerConfig>>,
    properties: Arc<RwLock<HashMap<ImmutableKey, Box<dyn RawProperty>>>>
}

impl DefaultConfigurationManager {
    pub fn new(config: Box<dyn ConfigurationManagerConfig>) -> DefaultConfigurationManager {
        let manager = DefaultConfigurationManager {
            config: Arc::new(config),
            properties: Arc::new(RwLock::new(HashMap::new()))
        };
        let clone = manager.clone();
        let source_change_listener: ConfigurationSourceChangeListener
            = Arc::new(Box::new(move |e|clone.on_source_change(e)));
        for s in manager.config.get_sources() {
            s.add_change_listener(source_change_listener.clone());
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
                    old_value.map(|v|ImmutableValue::wrap(v)),
                    new_value.as_ref().map(|v|ImmutableValue::wrap(v.clone())), event.get_change_time());
                let raw_property = property.as_ref().clone();
                let default_raw_property = raw_property.as_any_ref().downcast_ref::<DefaultRawProperty>().unwrap().clone();
                default_raw_property.set_value(new_value);
                let action: Box<dyn Fn()> = Box::new(move || default_raw_property.raise_change_event(&pe));
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
        let key = ImmutableKey::wrap(config.get_key());
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

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>> {
        for source in self.config.get_sources().iter() {
            let value = source.get_property_value(config);
            if value.is_none() {
                continue;
            }

            if config.get_value_filter().is_none() {
                return value;
            }

            let value = config.get_value_filter().unwrap().filter(value.unwrap());
            if value.is_some() {
                return value;
            }
        }

        None
    }

as_boxed!(impl ConfigurationManager);
}

impl PartialEq for DefaultConfigurationManager {
    fn eq(&self, other: &Self) -> bool {
        self.properties.as_ref().reference_equals(other.properties.as_ref())
    }
}

impl Eq for DefaultConfigurationManager {

}

unsafe impl Sync for DefaultConfigurationManager { }
unsafe impl Send for DefaultConfigurationManager { }

#[cfg(test)]
mod test {
    use super::*;
    use std::thread;
    use lang_extension::convert::*;
    use crate::source::default::*;
    use crate::facade::*;

    #[test]
    fn manager_test() {
        let source = DefaultConfigurationSource::new(
            DefaultConfigurationSourceConfigBuilder::new().set_name("test").build(),
            Arc::new(Box::new(move |o| -> Option<Box<dyn Value>> {
                let x = if Key::as_trait_ref(&"key_ok".to_string()).equals(o.as_any_ref()) {
                    Some(Value::to_boxed("10".to_string()))
                } else if Key::as_trait_ref(&"key_error".to_string()).equals(o.as_any_ref()) {
                    Some(Value::to_boxed("error".to_string()))
                } else {
                    None
                };
                println!("raw_value: {:?}", x);
                x
            })));
        let config = DefaultConfigurationManagerConfigBuilder::new()
            .set_name("test").add_source(1, Box::new(source)).build();
        let manager = DefaultConfigurationManager::new(config);
        let value_converter = DefaultTypeConverter::<String, i32>::new(Box::new(|s|{
            match s.parse::<i32>() {
                Ok(v) => {
                    println!("parse value: {}", v);
                    Ok(Box::new(v))
                },
                Err(e) => {
                    println!("parse error: {}", e);
                    Err(Box::new(e.to_string()))
                }
            }
        }));
        let config = DefaultPropertyConfigBuilder::<String, i32>::new().set_key("key_ok".to_string())
            .add_value_converter(RawTypeConverter::clone_boxed(&value_converter))
            .set_value_filter(Box::new(DefaultValueFilter::<i32>::new(
                Box::new(|v|if *v == 10 { Some(Box::new(5)) } else { Some(v) }))))
            .build();
        let property = manager.get_property(RawPropertyConfig::as_trait_ref(config.as_ref()));
        println!("config: {:?}", config);
        println!("property: {:?}", property);
        println!("value: {:?}", property.get_value());

        let config2 = DefaultPropertyConfigBuilder::<String, i32>::new().set_key("key_error".to_string())
            .add_value_converter(RawTypeConverter::clone_boxed(&value_converter)).build();
        let property2 = manager.get_property(RawPropertyConfig::as_trait_ref(config2.as_ref()));
        println!("config: {:?}", config2);
        println!("property: {:?}", property2);
        println!("value: {:?}", property2.get_value());

        let handle = thread::spawn(move || {
            let property = manager.get_property(RawPropertyConfig::as_trait_ref(config.as_ref()));
            println!("config: {:?}", config);
            println!("property: {:?}", property);
            println!("value: {:?}", property.get_value());
        });
        handle.join().unwrap();
    }

    #[test]
    fn dynamic_test() {
        let memory_map = Arc::new(RwLock::new(HashMap::<String, String>::new()));
        memory_map.write().unwrap().insert("key_ok".to_string(), "10".to_string());
        memory_map.write().unwrap().insert("key_error".to_string(), "error".to_string());
        let memory_map2 = memory_map.clone();
        let source = DefaultConfigurationSource::new(
            DefaultConfigurationSourceConfigBuilder::new().set_name("test").build(),
            Arc::new(Box::new(move |o| -> Option<Box<dyn Value>> {
                memory_map2.read().unwrap().get(o.as_any_ref().downcast_ref::<String>().unwrap()).map(|v|{
                    Value::clone_boxed(v)
                })
            })));
        let config = DefaultConfigurationManagerConfigBuilder::new()
            .set_name("test").add_source(1, Box::new(source.clone())).build();
        let manager = DefaultConfigurationManager::new(config);
        let value_converter = DefaultTypeConverter::<String, i32>::new(Box::new(|s|{
            match s.parse::<i32>() {
                Ok(v) => {
                    Ok(Box::new(v))
                },
                Err(e) => {
                    Err(Box::new(e.to_string()))
                }
            }
        }));
        let config = DefaultPropertyConfigBuilder::<String, i32>::new().set_key("key_ok".to_string())
            .add_value_converter(RawTypeConverter::clone_boxed(&value_converter))
            .set_value_filter(Box::new(DefaultValueFilter::<i32>::new(
                Box::new(|v|if *v == 10 { Some(Box::new(5)) } else { Some(v) }))))
            .build();
        let property = manager.get_property(RawPropertyConfig::as_trait_ref(config.as_ref()));
        println!("config: {:?}", config);
        println!("property: {:?}", property);
        println!("value: {:?}", property.get_value());

        println!();

        memory_map.write().unwrap().insert("key_ok".to_string(), "11".to_string());
        source.raise_change_event();
        println!("property: {:?}, value: {:?}", property, property.get_value());
        println!();

        let properties = ConfigurationProperties::new(Box::new(manager));
        let property = properties.get_property(config.as_ref());
        Property::<String, i32>::add_change_listener(property.as_ref(), Arc::new(Box::new(|e| {
            println!("changed: {:?}", e);
            println!();
        })));
        memory_map.write().unwrap().insert("key_ok".to_string(), "12".to_string());
        source.raise_change_event();
        println!("property: {:?}, value: {:?}", property,
            Property::<String, i32>::get_value(property.as_ref()));
        println!();
    }

}
