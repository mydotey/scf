use std::time::SystemTime;
use crate::value::*;

pub mod default;

pub trait PropertyConfig<K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    fn get_key(&self) -> &K;

    fn get_default_value(&self) -> &V;
}

pub trait Property<K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    fn get_config(&self) -> &PropertyConfig<K, V>;

    fn get_value(&self) -> Option<&V>;

    fn add_change_listener(&mut self, listener: PropertyChangeListener<K, V>);
}

pub trait PropertyChangeEvent<K, V>
    where
        K: KeyConstraints,
        V: ValueConstraints
{
    fn get_property(&self) -> &Property<K, V>;

    fn get_old_value(&self) -> Option<&V>;

    fn get_new_value(&self) -> Option<&V>;

    fn get_change_time(&self) -> &SystemTime;
}

pub type PropertyChangeListener<K, V> = Box<FnMut(&PropertyChangeEvent<K, V>)>;
