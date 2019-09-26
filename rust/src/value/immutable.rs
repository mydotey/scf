use std::hash::{ Hash, Hasher };
use std::any::Any;
use std::fmt::{ Display, Formatter, Result, Debug };
use std::sync::Arc;
use std::ops::Deref;
use std::convert::AsRef;

use super::{ Value, any::* };

#[derive(Clone)]
pub struct Immutable {
    value: Arc<Box<Any>>,
    hash_caculator: Arc<Box<HashCaculator>>,
    equality_caculator: Arc<Box<EqualityCaculator>>,
    to_string_caculator: Arc<Box<ToStringCaculator>>,
    clone_maker: Arc<Box<CloneMaker>>
}

impl Immutable {
    pub fn new<T: Value>(value: T) -> Immutable {
        Immutable {
            value: Arc::new(Box::new(value)),
            hash_caculator: Arc::new(Box::new(hash::<T>)),
            equality_caculator: Arc::new(Box::new(equal::<T>)),
            to_string_caculator: Arc::new(Box::new(to_string::<T>)),
            clone_maker: Arc::new(Box::new(clone::<T>))
        }
    }
}

impl Hash for Immutable {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.hash_caculator.as_ref()(self.as_ref()));
    }
}

impl PartialEq for Immutable {
    fn eq(&self, other: &Self) -> bool {
        self.equality_caculator.as_ref()(self.as_ref(), other.as_ref())
    }
}

impl Eq for Immutable {

}

impl Display for Immutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.as_ref()))
    }
}

impl Debug for Immutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.as_ref()))
    }
}

unsafe impl Send for Immutable {

}

unsafe impl Sync for Immutable {

}

impl Into<Box<Any>> for Immutable {

    fn into(self) -> Box<Any> {
        self.clone_maker.as_ref()(self.as_ref())
    }

}

impl Deref for Immutable {
    type Target = Any;

    fn deref(&self) -> &Self::Target {
        self.as_ref()
    }
}

impl AsRef<Any> for Immutable {

    fn as_ref(&self) -> &Any {
        self.value.as_ref().as_ref()
    }

}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;

    #[test]
    fn object() {
        assert_eq!(Immutable::new("key"), Immutable::new("key"));
        assert_ne!(Immutable::new("key"), Immutable::new("value"));
    }

    #[test]
    fn generic_map() {
        let mut map = HashMap::<Immutable, Immutable>::new();
        map.insert(Immutable::new("key"), Immutable::new("value"));
        println!("map: {}", map.get(&Immutable::new("key")).unwrap());
    }

}