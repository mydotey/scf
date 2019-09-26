
use crate::property::*;
use std::fmt;

pub struct DefaultPropertyConfig<K: Value, V: Value> {
    key: K,
    default_value: V
}

impl <K: Value, V: Value> PropertyConfig<K, V> for DefaultPropertyConfig<K, V> {
    fn get_key(&self) -> &K {
        &self.key
    }

    fn get_default_value(&self) -> &V {
        &self.default_value
    }
}

impl <K: Value, V: Value> DefaultPropertyConfig<K, V> {
    pub fn new(key: K, default_value: V) -> Self {
        DefaultPropertyConfig {
            key,
            default_value
        }
    }
}

impl <K: Value, V: Value> fmt::Display for DefaultPropertyConfig<K, V> {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ key: {}, default_value: {} }}", self.key, self.default_value)
    }
}

pub struct DefaultProperty<K: Value, V: Value> {
    config: Box<PropertyConfig<K, V>>,
    value: Option<V>,
    change_listeners: Vec<PropertyChangeListener<K, V>>
}

impl <K: Value, V: Value> DefaultProperty<K, V> {
    pub fn new(config: impl PropertyConfig<K, V> + 'static) -> Self {
        DefaultProperty {
            config: Box::new(config),
            value: None,
            change_listeners: Vec::new()
        }
    }

    pub fn set_value(&mut self, value: V) {
        self.value = Some(value);
    }
}

impl <K: Value, V: Value> Property<K, V> for DefaultProperty<K, V> {
    fn get_config(&self) -> &PropertyConfig<K, V> {
        self.config.as_ref()
    }

    fn get_value(&self) -> Option<&V> {
        self.value.as_ref()
    }

    fn add_change_listener(&mut self, listener: PropertyChangeListener<K, V>) {
        self.change_listeners.push(listener);
    }
}

pub struct DefaultPropertyChangeEvent<'r, K: Value, V: Value> {
    property: &'r Property<K, V>,
    old_value: Option<V>,
    change_time: SystemTime
}

impl <'r, K: Value, V: Value> DefaultPropertyChangeEvent<'r, K, V> {
    pub fn new(property: &'r Property<K, V>, old_value: Option<V>, change_time: SystemTime) -> Self {
        DefaultPropertyChangeEvent {
            property,
            old_value,
            change_time
        }
    }
}

impl <'r, K: Value, V: Value> PropertyChangeEvent<K, V> for DefaultPropertyChangeEvent<'r, K, V> {
    fn get_property(&self) -> &Property<K, V> {
        self.property
    }

    fn get_old_value(&self) -> Option<&V> {
        self.old_value.as_ref()
    }

    fn get_new_value(&self) -> Option<&V> {
        self.property.get_value()
    }

    fn get_change_time(&self) -> &SystemTime {
        &self.change_time
    }
}

#[cfg(test)]
mod test {

    use super::*;

    #[test]
    fn test() {
        let config = DefaultPropertyConfig::new(1, 2);
        println!("{}", config);
    }

}
