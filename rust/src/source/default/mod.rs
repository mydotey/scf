use std::sync::{ Arc, RwLock };
use std::fmt;
use std::time::*;

use super::*;

pub type PropertyProvider = FunctionRef<dyn Key, Option<Box<dyn Value>>>;

#[derive(PartialEq, Eq, Debug, Clone)]
pub struct DefaultConfiguratonSourceConfig {
    name: String
}

impl ConfigurationSourceConfig for DefaultConfiguratonSourceConfig {
    fn get_name(&self) -> &str {
        self.name.as_str()
    }

as_boxed!(impl ConfigurationSourceConfig);
}

pub struct DefaultConfigurationSourceConfigBuilder {
    name: Option<String>
}

impl DefaultConfigurationSourceConfigBuilder {
    pub fn new() -> Self {
        Self {
            name: None
        }
    }
}

impl ConfigurationSourceConfigBuilder for DefaultConfigurationSourceConfigBuilder {
    fn set_name(&mut self, name: &str) -> &mut dyn ConfigurationSourceConfigBuilder {
        self.name = Some(name.to_owned());
        self
    }

    fn build(&self) -> Box<dyn ConfigurationSourceConfig> {
        Box::new(DefaultConfiguratonSourceConfig {
            name: self.name.as_ref().unwrap().to_owned()
        })
    }
}

#[derive(Clone)]
pub struct DefaultConfigurationSource {
    config: Arc<Box<dyn ConfigurationSourceConfig>>,
    property_provider: Arc<RwLock<PropertyProvider>>,
    listeners: Arc<RwLock<Vec<ConfigurationSourceChangeListener>>>
}

impl DefaultConfigurationSource {
    pub fn new(config: Box<dyn ConfigurationSourceConfig>, property_provider: PropertyProvider)
        -> DefaultConfigurationSource {
        DefaultConfigurationSource {
            config: Arc::new(config),
            property_provider: Arc::new(RwLock::new(property_provider)),
            listeners: Arc::new(RwLock::new(Vec::new()))
        }
    }

    pub fn raise_change_event(&self) {
        let event = DefaultConfigurationSourceChangeEvent::new(self,
            SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_millis());
        let lock = self.listeners.read().unwrap();
        for listener in lock.iter() {
            listener(&event);
        }
    }
}

impl PartialEq for DefaultConfigurationSource {
    fn eq(&self, other: &Self) -> bool {
        self.property_provider.as_ref().reference_equals(other.property_provider.as_ref())
    }
}

impl Eq for DefaultConfigurationSource {

}

impl fmt::Debug for DefaultConfigurationSource {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{} {{ config: {:?} }}", self.type_name(), self.config)
    }
}

unsafe impl Sync for DefaultConfigurationSource { }
unsafe impl Send for DefaultConfigurationSource { }

impl ConfigurationSource for DefaultConfigurationSource {
    fn get_config(&self) -> &dyn ConfigurationSourceConfig {
        self.config.as_ref().as_ref()
    }

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>> {
        let lock = self.property_provider.read().unwrap();
        match lock(config.get_raw_key().as_ref()) {
            Some(v) => {
                let type_id = v.as_ref().type_id();
                if type_id == config.get_value_type() {
                    return Some(v);
                } else {
                    for value_converter in config.get_value_converters() {
                        match value_converter.convert_raw(v.as_ref()) {
                            Ok(v_t) => {
                                debug!("property value converted by converter, from {:?} to {:?}, \
                                    property: {:?}, converter: {:?}", v.as_ref(), v_t.as_ref(),
                                    config, value_converter);
                                return Some(v_t);
                            },
                            Err(error) => {
                                debug!("property value cannot be converted by converter, value: {:?}, \
                                    property: {:?}, converter: {:?}, convert error: {:?}",
                                    v.as_ref(), config, value_converter, error);
                            }
                        } 
                    }
                }
                None
            },
            None => None
        }
    }

    fn add_change_listener(&self, listener: ConfigurationSourceChangeListener) {
        let mut lock = self.listeners.write().unwrap();
        lock.push(listener);
    }

as_boxed!(impl ConfigurationSource);
}

#[derive(Eq, Debug, Clone)]
pub struct DefaultConfigurationSourceChangeEvent {
    source: Box<dyn ConfigurationSource>,
    change_time: u128
}

impl DefaultConfigurationSourceChangeEvent {
    pub fn new(source: &dyn ConfigurationSource, change_time: u128) -> Self {
        DefaultConfigurationSourceChangeEvent {
            source: ConfigurationSource::clone_boxed(source),
            change_time
        }
    }
}

impl PartialEq for DefaultConfigurationSourceChangeEvent {
    fn eq(&self, other: &Self) -> bool {
        self.source.eq(&other.source) && self.change_time == other.change_time
    }
}

unsafe impl Sync for DefaultConfigurationSourceChangeEvent { }
unsafe impl Send for DefaultConfigurationSourceChangeEvent { }

impl ConfigurationSourceChangeEvent for DefaultConfigurationSourceChangeEvent {

    fn get_source(&self) -> &dyn ConfigurationSource {
        self.source.as_ref()
    }

    fn get_change_time(&self) -> u128 {
        self.change_time
    }

as_boxed!(impl ConfigurationSourceChangeEvent);
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::property::default::*;
    use std::sync::atomic::*;
    use crate::tests::init_log;
    use lang_extension::convert::*;

    #[test]
    fn source_config_test() {
        let mut builder = DefaultConfigurationSourceConfigBuilder::new();
        let config = builder.set_name("test").build();
        println!("config: {:?}", config);
        assert_eq!("test", config.get_name());
        assert_eq!(&config, &config.clone());
    }

    #[test]
    fn source_test() {
        init_log();

        let property_provider: PropertyProvider = Arc::new(Box::new(|k|{
            if k.equals("10".to_string().as_any_ref()) {
                Some(Value::to_boxed(10))
            } else if k.equals("11".to_string().as_any_ref()) {
                Some(Value::to_boxed("11".to_string()))
            } else if k.equals("xx".to_string().as_any_ref()) {
                Some(Value::to_boxed("xx"))
            } else {
                None
            }
        }));
        let mut builder = DefaultConfigurationSourceConfigBuilder::new();
        let config = builder.set_name("test").build();
        let source = DefaultConfigurationSource::new(config.clone(), property_provider);
        println!("config source: {:?}", source);
        assert_eq!(&config, &ConfigurationSourceConfig::clone_boxed(source.get_config()));

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
        let property_config = DefaultPropertyConfigBuilder::<String, i32>::new()
            .set_key(Box::new("10".to_string())).build();
        assert_eq!(Some(Value::to_boxed(10)), source.get_property_value(
            RawPropertyConfig::as_trait_ref(property_config.as_ref())));
        let property_config2 = DefaultPropertyConfigBuilder::<String, i32>::new()
            .set_key(Box::new("1".to_string())).build();
        assert_eq!(None, source.get_property_value(
            RawPropertyConfig::as_trait_ref(property_config2.as_ref())));
        let property_config3 = DefaultPropertyConfigBuilder::<i32, i32>::new()
            .set_key(Box::new(1)).build();
        assert_eq!(None, source.get_property_value(
            RawPropertyConfig::as_trait_ref(property_config3.as_ref())));
        let property_config4 = DefaultPropertyConfigBuilder::<String, i32>::new()
            .set_key(Box::new("11".to_string()))
            .add_value_converter(Box::new(value_converter.clone())).build();
        assert_eq!(Some(Value::to_boxed(11)), source.get_property_value(
            RawPropertyConfig::as_trait_ref(property_config4.as_ref())));
        let property_config5 = DefaultPropertyConfigBuilder::<String, i32>::new()
            .set_key(Box::new("xx".to_string()))
            .add_value_converter(Box::new(value_converter.clone())).build();
        assert_eq!(None, source.get_property_value(
            RawPropertyConfig::as_trait_ref(property_config5.as_ref())));

        let changed = Arc::new(AtomicBool::default());
        let changed_clone = changed.clone();
        source.add_change_listener(Arc::new(Box::new(move |e|{
            println!("event: {:?}", e);
            changed_clone.swap(true, Ordering::Relaxed);
        })));

        source.raise_change_event();
        assert!(changed.fetch_and(true, Ordering::Relaxed));
    }

    #[test]
    fn source_event_test() {
        let property_provider: PropertyProvider = Arc::new(Box::new(|k|{
            if k.equals("10".to_string().as_any_ref()) {
                Some(Value::to_boxed(10))
            } else {
                None
            }
        }));
        let mut builder = DefaultConfigurationSourceConfigBuilder::new();
        let config = builder.set_name("test").build();
        let source = DefaultConfigurationSource::new(config.clone(), property_provider);
 
        let start = SystemTime::now();
        let since_the_epoch = start.duration_since(UNIX_EPOCH).unwrap();
        let event = DefaultConfigurationSourceChangeEvent::new(&source, since_the_epoch.as_millis());
        println!("source event: {:?}", event);

        assert!(source.equals(event.get_source().as_any_ref()));
        assert_eq!(since_the_epoch.as_millis(), event.get_change_time());
    }
}