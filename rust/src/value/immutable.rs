use std::hash::{ Hash, Hasher };
use std::any::Any;
use std::fmt::{ Display, Formatter, Result, Debug };
use std::sync::Arc;

use super::{ Object, any::* };

#[derive(Clone)]
pub struct ImmutableObject {
    value: Arc<Box<Any>>,
    hash_caculator: Arc<Box<HashCaculator>>,
    equality_caculator: Arc<Box<EqualityCaculator>>,
    to_string_caculator: Arc<Box<ToStringCaculator>>,
    to_debug_string_caculator: Arc<Box<ToStringCaculator>>,
    clone_maker: Arc<Box<CloneMaker>>
}

impl ImmutableObject {
    pub fn new<T: Object>(value: T) -> ImmutableObject {
        ImmutableObject {
            value: Arc::new(Box::new(value)),
            hash_caculator: Arc::new(Box::new(hash::<T>)),
            equality_caculator: Arc::new(Box::new(equal::<T>)),
            to_string_caculator: Arc::new(Box::new(to_string::<T>)),
            to_debug_string_caculator: Arc::new(Box::new(to_string::<T>)),
            clone_maker: Arc::new(Box::new(clone::<T>))
        }
    }

    pub fn value(&self) -> Box<Any> {
        self.clone_maker.as_ref()(self.as_ref())
    }

    fn as_ref(&self) -> &Any {
        self.value.as_ref().as_ref()
    }
}

impl Hash for ImmutableObject {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.hash_caculator.as_ref()(self.as_ref()));
    }
}

impl PartialEq for ImmutableObject {
    fn eq(&self, other: &Self) -> bool {
        self.equality_caculator.as_ref()(self.as_ref(), other.as_ref())
    }
}

impl Eq for ImmutableObject {

}

impl Display for ImmutableObject {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.as_ref()))
    }
}

impl Debug for ImmutableObject {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_debug_string_caculator.as_ref()(self.as_ref()))
    }
}

unsafe impl Send for ImmutableObject {

}

unsafe impl Sync for ImmutableObject {

}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;

    #[test]
    fn object() {
        assert_eq!(ImmutableObject::new("key"), ImmutableObject::new("key"));
        assert_ne!(ImmutableObject::new("key"), ImmutableObject::new("value"));
    }

    #[test]
    fn generic_map() {
        let mut map = HashMap::<ImmutableObject, ImmutableObject>::new();
        map.insert(ImmutableObject::new("key"), ImmutableObject::new("value"));
        println!("map: {}", map.get(&ImmutableObject::new("key")).unwrap());
    }

}