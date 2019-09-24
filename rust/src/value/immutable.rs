use std::hash::{ Hash, Hasher };
use std::any::Any;
use std::collections::hash_map::DefaultHasher;
use std::fmt::{ Display, Formatter, Result, Debug };
use std::sync::Arc;

use super::object::*;

#[derive(Clone)]
pub struct Immutable {
    value: Arc<Box<Any>>,
    hash_caculator: Arc<Box<HashCaculator>>,
    equality_caculator: Arc<Box<EqualityCaculator>>,
    to_string_caculator: Arc<Box<ToStringCaculator>>,
    clone_maker: Arc<Box<CloneMaker>>
}

impl Immutable {
    pub fn new<T: 'static + Hash + Eq + Display + Clone>(value: T) -> Immutable {
        let value = Box::new(value);
        let mut hasher = DefaultHasher::default();
        value.hash(&mut hasher);
        let hash_caculator = |me: &Any| {
            let mut hasher = DefaultHasher::default();
            me.downcast_ref::<T>().unwrap().hash(&mut hasher);
            hasher.finish()
        };
        let equality_caculator = |me: &Any, other: &Any| {
            let (me, other) = (me.downcast_ref::<T>(), other.downcast_ref::<T>());
            match me {
                Some(r) => {
                    match other {
                        Some(r2) => *r == *r2,
                        None => false
                    }
                },
                None => {
                    match other {
                        None => true,
                        Some(_) => false
                    }
                }
            }
        };
        let to_string_caculator = |me: &Any| {
            match me.downcast_ref::<T>() {
                Some(r) => format!("{}", r),
                None => format!("{:?}", me)
            }
        };
        let clone_maker = |me: &Any| -> Box<Any> {
            Box::new(me.downcast_ref::<T>().unwrap().clone())
        };

        Immutable {
            value: Arc::new(value),
            hash_caculator: Arc::new(Box::new(hash_caculator)),
            equality_caculator: Arc::new(Box::new(equality_caculator)),
            to_string_caculator: Arc::new(Box::new(to_string_caculator)),
            clone_maker: Arc::new(Box::new(clone_maker))
        }
    }

    pub fn value(&self) -> Box<Any> {
        self.clone_maker.as_ref()(self.value.as_ref())
    }

}

impl Hash for Immutable {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.hash_caculator.as_ref()(self.value.as_ref()));
    }
}

impl PartialEq for Immutable {
    fn eq(&self, other: &Self) -> bool {
        self.equality_caculator.as_ref()(self.value.as_ref(), other.value.as_ref())
    }
}

impl Eq for Immutable {

}

impl Display for Immutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.value.as_ref()))
    }
}

impl Debug for Immutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.value.as_ref()))
    }
}

unsafe impl Send for Immutable {

}

unsafe impl Sync for Immutable {

}

impl Object for Immutable {

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