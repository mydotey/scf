use std::collections::HashMap;
use std::fmt;
use std::sync::Arc;

use lang_extension::any::*;
use scf_core::property::*;
use scf_core::source::default::*;
use scf_core::source::*;

#[derive(Clone)]
pub struct TestConfigurationSource {
    source: DefaultConfigurationSource,
}

impl TestConfigurationSource {
    pub fn new(
        config: Box<dyn ConfigurationSourceConfig>,
        properties: HashMap<String, String>,
    ) -> Self {
        let property_provider: PropertyProvider = Arc::new(Box::new(move |k| {
            match k.as_any_ref().downcast_ref::<String>() {
                Some(k) => properties.get(k).map(|v| Value::clone_boxed(v)),
                None => None,
            }
        }));
        let source = DefaultConfigurationSource::new(config, property_provider);
        Self { source }
    }
}

impl PartialEq for TestConfigurationSource {
    fn eq(&self, other: &Self) -> bool {
        self.source == other.source
    }
}

impl Eq for TestConfigurationSource {}

impl fmt::Debug for TestConfigurationSource {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(
            f,
            "{} {{ config: {:?} }}",
            self.type_name(),
            self.source.get_config()
        )
    }
}

unsafe impl Sync for TestConfigurationSource {}
unsafe impl Send for TestConfigurationSource {}

impl ConfigurationSource for TestConfigurationSource {
    fn get_config(&self) -> &dyn ConfigurationSourceConfig {
        self.source.get_config()
    }

    fn get_property_value(&self, config: &dyn RawPropertyConfig) -> Option<Box<dyn Value>> {
        self.source.get_property_value(config)
    }

    fn add_change_listener(&self, listener: ConfigurationSourceChangeListener) {
        self.source.add_change_listener(listener);
    }

    as_boxed!(impl ConfigurationSource);
}
