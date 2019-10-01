use lang_extension::value::*;

pub mod default;

pub trait PropertyConfig<K: ObjectConstraits, V: ObjectConstraits>: Object + Send + Sync {
    fn get_key(&self) -> K;

    fn get_default_value(&self) -> Option<V>;
}

pub trait Property<K: ObjectConstraits, V: ObjectConstraits>: Object + Send + Sync {
    fn get_config(&self) -> &dyn PropertyConfig<K, V>;

    fn get_value(&self) -> Option<V>;

    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>);
}

pub trait PropertyChangeEvent<K: ObjectConstraits, V: ObjectConstraits>: Object + Send + Sync {
    fn get_property(&self) -> &dyn Property<K, V>;

    fn get_old_value(&self) -> Option<V>;

    fn get_new_value(&self) -> Option<V>;

    fn get_change_time(&self) -> u64;
}

pub type PropertyChangeListener<K, V> = Box<dyn FnMut(&dyn PropertyChangeEvent<K, V>)>;
