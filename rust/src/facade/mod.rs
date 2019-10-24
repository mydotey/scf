use lang_extension::any::*;
use std::marker::PhantomData;
use std::sync::Arc;

use crate::property::*;
use crate::property::default::*;
use crate::source::*;
use crate::source::default::*;
use crate::manager::*;
use crate::manager::default::*;

pub struct ConfigurationManagers {
    _placeholder: PhantomData<i32>
}

impl ConfigurationManagers {
    pub fn new_config_builder() -> Box<dyn ConfigurationManagerConfigBuilder> {
        Box::new(DefaultConfigurationManagerConfigBuilder::new())
    }

    pub fn new_manager(config: Box<dyn ConfigurationManagerConfig>) -> Box<dyn ConfigurationManager> {
        Box::new(DefaultConfigurationManager::new(config))
    }
}

#[derive(PartialEq, Eq, Debug, Clone)]
pub struct ConfigurationProperties {
    manager: Arc<Box<dyn ConfigurationManager>>
}

unsafe impl Sync for ConfigurationProperties { }
unsafe impl Send for ConfigurationProperties { }

impl ConfigurationProperties {
    pub fn new_config_builder<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>()
        -> Box<dyn PropertyConfigBuilder<K, V>> {
        Box::new(DefaultPropertyConfigBuilder::new())
    }

    pub fn new(manager: Box<dyn ConfigurationManager>) -> Self {
        Self {
            manager: Arc::new(manager)
        }
    }

    pub fn get_property<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>(&self,
        config: &dyn PropertyConfig<K, V>) -> Box<dyn Property<K, V>> {
        let p = self.manager.get_property(RawPropertyConfig::as_trait_ref(config));
        Property::<K, V>::to_boxed(DefaultProperty::from_raw(p.as_ref()))
    }

    pub fn get_property_value<K: ?Sized + KeyConstraint, V: ?Sized + ValueConstraint>(&self,
        config: &dyn PropertyConfig<K, V>) -> Option<V> {
        match self.manager.get_property_value(RawPropertyConfig::as_trait_ref(config)) {
            Some(v) => match v.as_ref().as_any_ref().downcast_ref::<V>() {
                    Some(v) => Some(v.clone()),
                    None => None
            },
            None => None
        }
    }
}

pub struct ConfigurationSources {
    _placeholder: PhantomData<i32>
}

impl ConfigurationSources {
    pub fn new_config_builder() -> Box<dyn ConfigurationSourceConfigBuilder> {
        Box::new(DefaultConfigurationSourceConfigBuilder::new())
    }

    pub fn new_source(config: Box<dyn ConfigurationSourceConfig>, property_provider: PropertyProvider)
        -> Box<dyn ConfigurationSource> 
    {
        ConfigurationSource::to_boxed(DefaultConfigurationSource::new(config, property_provider))
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use lang_extension::convert::*;
    use std::any::*;
    use std::sync::atomic::*;
    use std::collections::HashMap;
    use std::sync::*;

    #[test]
    fn new_property_config() {
        let c = DefaultTypeConverter::<String, i32>::new(
            Box::new(move |v| match v.parse::<i32>() {
                Ok(v) => Ok(Box::new(v)),
                Err(err) => Err(Box::new(err))
            }));
        let f = DefaultValueFilter::new(Box::new(move |v| {
            if *v > 10 { Some(Box::new(*v + 1)) } else if *v > 0 { Some(v) } else { None }
        }));

        let mut builder = ConfigurationProperties::new_config_builder::<String, i32>();
        let config = builder.set_key("test".to_string()).set_default_value(0)
            .set_value_filter(ValueFilter::to_boxed(f))
            .add_value_converter(RawTypeConverter::to_boxed(c)).build();
        println!("property config: {:?}", config);
        assert_eq!("test".to_string(), config.get_key());
        assert_eq!(0.type_id(), config.get_value_type());
        assert_eq!(Some(0), config.get_default_value());
        assert_eq!(Some(Value::to_boxed(12)),
            config.get_value_filter().unwrap().filter_raw(Value::to_boxed(11)));
        assert_eq!(Ok(Value::to_boxed(10)),
            config.get_value_converters().get(0).unwrap().convert_raw(Value::as_trait_ref(&"10".to_string())));
    }

    #[test]
    fn new_source_config() {
        let mut builder = ConfigurationSources::new_config_builder();
        let config = builder.set_name("test").build();
        println!("source config: {:?}", config);
        assert_eq!("test", config.get_name());
    }

    #[test]
    fn new_source() {
        let mut builder = ConfigurationSources::new_config_builder();
        let config = builder.set_name("test").build();
        let property_provider: PropertyProvider = Arc::new(Box::new(|k|{
            if k.equals("10".to_string().as_any_ref()) {
                Some(Value::to_boxed(10))
            } else {
                None
            }
        }));
        let source = ConfigurationSources::new_source(config, property_provider);
        println!("configuration source: {:?}", source);
        assert_eq!("test", source.get_config().get_name());
        let property_config = ConfigurationProperties::new_config_builder::<String, i32>()
            .set_key("10".to_string()).build();
        assert_eq!(Some(Value::to_boxed(10)), source.get_property_value(
            RawPropertyConfig::as_trait_ref(property_config.as_ref())));
        let property_config = ConfigurationProperties::new_config_builder::<String, i32>()
            .set_key("11".to_string()).build();
        assert_eq!(None, source.get_property_value(
            RawPropertyConfig::as_trait_ref(property_config.as_ref())));
    }

    #[test]
    fn new_manager_config() {
        let mut builder = ConfigurationSources::new_config_builder();
        let config = builder.set_name("test").build();
        let property_provider: PropertyProvider = Arc::new(Box::new(|k|{
            if k.equals("10".to_string().as_any_ref()) {
                Some(Value::to_boxed(10))
            } else {
                None
            }
        }));
        let source = ConfigurationSources::new_source(config, property_provider);
        let mut builder = ConfigurationManagers::new_config_builder();
        let config = builder.set_name("test-manager").add_source(1, source).build();
        println!("manager config: {:?}", config);
        assert_eq!("test-manager", config.get_name());
        assert_eq!(1, config.get_sources().len());
        let changed = Arc::new(AtomicBool::default());
        let changed_clone = changed.clone();
        let action: Box<dyn Fn()> = Box::new(move ||{
            changed_clone.swap(true, Ordering::Relaxed);
            println!("OK");
        });
        config.get_task_executor()(&action);
        assert_eq!(true, changed.fetch_and(true, Ordering::Relaxed));
    }

    #[test]
    fn new_manager() {
        let mut builder = ConfigurationSources::new_config_builder();
        let source_config = builder.set_name("test").build();
        let property_provider: PropertyProvider = Arc::new(Box::new(|k|{
            if k.equals("10".to_string().as_any_ref()) {
                Some(Value::to_boxed(10))
            } else {
                None
            }
        }));
        let source = ConfigurationSources::new_source(source_config, property_provider);
        let mut builder = ConfigurationManagers::new_config_builder();
        let config = builder.set_name("test-manager").add_source(1, source).build();
        let manager = ConfigurationManagers::new_manager(config);
        println!("manager: {:?}", manager);
        assert_eq!("test-manager", manager.get_config().get_name());

        let c = DefaultTypeConverter::<String, i32>::new(
            Box::new(move |v| match v.parse::<i32>() {
                Ok(v) => Ok(Box::new(v)),
                Err(err) => Err(Box::new(err))
            }));
        let f = DefaultValueFilter::new(Box::new(move |v| {
            if *v > 10 { Some(Box::new(*v + 1)) } else if *v > 0 { Some(v) } else { None }
        }));
        let mut builder = ConfigurationProperties::new_config_builder::<String, i32>();
        let property_config = builder.set_key("10".to_string()).set_default_value(0)
            .set_value_filter(ValueFilter::to_boxed(f))
            .add_value_converter(RawTypeConverter::to_boxed(c)).build();
        let property = manager.get_property(RawPropertyConfig::as_trait_ref(property_config.as_ref()));
        assert_eq!(Some(Value::to_boxed(10)), property.get_raw_value());

        let value = manager.get_property_value(RawPropertyConfig::as_trait_ref(property_config.as_ref()));
        assert_eq!(Some(Value::to_boxed(10)), value);
    }

    #[test]
    fn properties() {
        let mut builder = ConfigurationSources::new_config_builder();
        let source_config = builder.set_name("test").build();
        let property_provider: PropertyProvider = Arc::new(Box::new(|k|{
            if k.equals("key_ok".to_string().as_any_ref()) {
                Some(Value::to_boxed(20))
            } else {
                None
            }
        }));
        let source = ConfigurationSources::new_source(source_config, property_provider);

        let memory_map = Arc::new(RwLock::new(HashMap::<String, String>::new()));
        memory_map.write().unwrap().insert("key_ok".to_string(), "10".to_string());
        memory_map.write().unwrap().insert("key_error".to_string(), "error".to_string());
        let memory_map2 = memory_map.clone();
        let source_config2 = ConfigurationSources::new_config_builder().set_name("dynamic_source").build();
        let source2 = DefaultConfigurationSource::new(
            source_config2,
            Arc::new(Box::new(move |o| -> Option<Box<dyn Value>> {
                match o.as_any_ref().downcast_ref::<String>() {
                    Some(k) => memory_map2.read().unwrap().get(k).map(|v|Value::clone_boxed(v)),
                    None => None
                }
            })));
 
        let mut builder = ConfigurationManagers::new_config_builder();
        let config = builder.set_name("test-manager").add_source(1, source)
            .add_source(2, Box::new(source2.clone())).build();
        let manager = ConfigurationManagers::new_manager(config);
        let properties = ConfigurationProperties::new(manager);
        println!("properties: {:?}", properties);

        let c = DefaultTypeConverter::<String, i32>::new(
            Box::new(move |v| match v.parse::<i32>() {
                Ok(v) => Ok(Box::new(v)),
                Err(err) => Err(Box::new(err))
            }));
        let f = DefaultValueFilter::new(Box::new(move |v| {
            if *v > 10 { Some(Box::new(*v + 1)) } else if *v > 0 { Some(v) } else { None }
        }));
        let mut builder = ConfigurationProperties::new_config_builder::<String, i32>();
        let property_config = builder.set_key("key_ok".to_string()).set_default_value(0)
            .set_value_filter(ValueFilter::clone_boxed(&f))
            .add_value_converter(RawTypeConverter::clone_boxed(&c)).build();
        let property = properties.get_property(property_config.as_ref());
        assert_eq!(Some(10), property.get_value());

        let property_config2 = builder.set_key("key_error".to_string()).set_default_value(0)
            .set_value_filter(ValueFilter::to_boxed(f))
            .add_value_converter(RawTypeConverter::to_boxed(c)).build();
        let property2 = properties.get_property(property_config2.as_ref());
        assert_eq!(None, property2.get_value());

        let changed = Arc::new(AtomicBool::new(false));
        let changed_clone = changed.clone();
        property.add_change_listener(Arc::new(Box::new(move |e| {
            println!("changed: {:?}", e);
            changed_clone.swap(true, Ordering::Relaxed);
        })));
        memory_map.write().unwrap().insert("key_ok".to_string(), "12".to_string());
        source2.raise_change_event();
        assert_eq!(Some(13), property.get_value());
        assert!(changed.fetch_and(true, Ordering::Relaxed));

        memory_map.write().unwrap().remove(&"key_ok".to_string());
        source2.raise_change_event();
        assert_eq!(Some(21), property.get_value());
    }

}