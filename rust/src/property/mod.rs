use lang_extension::object::*;

pub mod default;

pub trait RawPropertyConfig: Object + Send + Sync {
    fn get_key(&self) -> Box<dyn Object>;

    fn get_default_value(&self) -> Option<Box<dyn Object>>;

    fn clone(&self) -> Box<dyn RawPropertyConfig>;
}

pub trait RawProperty: Object + Send + Sync {
    fn get_config(&self) -> &dyn RawPropertyConfig;

    fn get_value(&self) -> Option<Box<dyn Object>>;

    fn add_change_listener(&self, listener: RawPropertyChangeListener);

    fn clone(&self) -> Box<dyn RawProperty>;
}

pub trait RawPropertyChangeEvent: Object + Send + Sync {
    fn get_property(&self) -> &dyn RawProperty;

    fn get_old_value(&self) -> Option<Box<dyn Object>>;

    fn get_new_value(&self) -> Option<Box<dyn Object>>;

    fn get_change_time(&self) -> u64;

    fn clone(&self) -> Box<dyn RawPropertyChangeEvent>;
}

pub type RawPropertyChangeListener = Box<dyn Fn(&dyn RawPropertyChangeEvent)>;

pub trait PropertyConfig<K: ObjectConstraits, V: ObjectConstraits> : RawPropertyConfig {
    fn get_key(&self) -> K;

    fn get_default_value(&self) -> Option<V>;

    fn clone(&self) -> Box<dyn PropertyConfig<K, V>>;

    fn as_raw(&self) -> & dyn RawPropertyConfig;
}

pub trait PropertyConfigBuilder<K: ObjectConstraits, V: ObjectConstraits> {
    fn set_key(&mut self, key: K) -> &mut dyn PropertyConfigBuilder<K, V>;

    fn set_default_value(&mut self, default_value: V) -> &mut dyn PropertyConfigBuilder<K, V>;

    fn build(&self) -> Box<dyn PropertyConfig<K, V>>;
}

pub trait Property<K: ObjectConstraits, V: ObjectConstraits> : RawProperty {
    fn get_config(&self) -> &dyn PropertyConfig<K, V>;

    fn get_value(&self) -> Option<V>;

    fn add_change_listener(&self, listener: PropertyChangeListener<K, V>);

    fn clone(&self) -> Box<dyn Property<K, V>>;

    fn as_raw(&self) -> & dyn RawProperty;
}

pub trait PropertyChangeEvent<K: ObjectConstraits, V: ObjectConstraits> : RawPropertyChangeEvent {
    fn get_property(&self) -> &dyn Property<K, V>;

    fn get_old_value(&self) -> Option<V>;

    fn get_new_value(&self) -> Option<V>;

    fn get_change_time(&self) -> u64;

    fn clone(&self) -> Box<dyn PropertyChangeEvent<K, V>>;

    fn as_raw(&self) -> & dyn RawPropertyChangeEvent;
}

pub type PropertyChangeListener<K, V> = Box<dyn Fn(&dyn PropertyChangeEvent<K, V>)>;
