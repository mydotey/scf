
use crate::property::*;
use std::fmt;

pub struct DefaultPropertyConfig<K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    key: K,
    default_value: V
}

impl <K, V> PropertyConfig<K, V> for DefaultPropertyConfig<K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    fn get_key(&self) -> &K {
        &self.key
    }

    fn get_default_value(&self) -> &V {
        &self.default_value
    }
}

impl <K, V> DefaultPropertyConfig<K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    pub fn new(key: K, default_value: V) -> Self {
        DefaultPropertyConfig {
            key,
            default_value
        }
    }
}

impl <K, V> fmt::Display for DefaultPropertyConfig<K, V>
    where
        K: KeyConstraints + fmt::Display,
        V: ValueConstraints + fmt::Display
{
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{{ key: {}, default_value: {} }}", self.key, self.default_value)
    }
}

pub struct DefaultProperty<K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    config: Box<PropertyConfig<K, V>>,
    value: Option<V>,
    change_listeners: Vec<PropertyChangeListener<K, V>>
}

impl <K, V> DefaultProperty<K, V> 
    where
        K: KeyConstraints,
        V: ValueConstraints
{
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

impl <K, V> Property<K, V> for DefaultProperty<K, V> 
    where
        K: KeyConstraints,
        V: ValueConstraints
{
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

pub struct DefaultPropertyChangeEvent<'r, K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    property: &'r Property<K, V>,
    old_value: Option<V>,
    change_time: SystemTime
}

impl <'r, K, V> DefaultPropertyChangeEvent<'r, K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    pub fn new(property: &'r Property<K, V>, old_value: Option<V>, change_time: SystemTime) -> Self {
        DefaultPropertyChangeEvent {
            property,
            old_value,
            change_time
        }
    }
}

impl <'r, K, V> PropertyChangeEvent<K, V> for DefaultPropertyChangeEvent<'r, K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
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
