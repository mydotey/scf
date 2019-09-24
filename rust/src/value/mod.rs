use std::hash::{ Hash, Hasher };
use std::any::Any;
use std::collections::hash_map::DefaultHasher;
use std::fmt::{ Display, Formatter, Result, Debug };
use std::sync::Arc;
use std::sync::RwLock;

pub trait KeyConstraints: 'static + Hash + Eq + Send + Sync { }

pub trait ValueConstraints: 'static + Eq + Send + Sync { }

impl<T: 'static + Hash + Eq + Send + Sync> KeyConstraints for T { }

impl<T: 'static + Eq + Send + Sync> ValueConstraints for T { }

type HashCaculator = Fn(&Any) -> u64;
type EqualityCaculator = Fn(&Any, &Any) -> bool;
type ToStringCaculator = Fn(&Any) -> String;
type CloneMaker = Fn(&Any) -> Box::<Any>;

pub struct Object {
    boxed_value: RwLock<Box<Any>>,
    hash_caculator: Arc<Box<HashCaculator>>,
    equality_caculator: Arc<Box<EqualityCaculator>>,
    to_string_caculator: Arc<Box<ToStringCaculator>>,
    clone_maker: Arc<Box<CloneMaker>>
}

impl Object {
    pub fn new<T: 'static + Hash + Eq + Display + Clone>(any: T) -> Object {
        let any = Box::new(any);
        let mut hasher = DefaultHasher::default();
        any.hash(&mut hasher);
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

        Object {
            boxed_value: RwLock::new(any),
            hash_caculator: Arc::new(Box::new(hash_caculator)),
            equality_caculator: Arc::new(Box::new(equality_caculator)),
            to_string_caculator: Arc::new(Box::new(to_string_caculator)),
            clone_maker: Arc::new(Box::new(clone_maker))
        }
    }

    pub fn value(&self) -> Box<Any> {
        let lock = self.boxed_value.read().unwrap();
        self.clone_maker.as_ref()(lock.as_ref())
    }

}

impl Hash for Object {
    fn hash<H: Hasher>(&self, state: &mut H) {
        let lock = self.boxed_value.read().unwrap();
        state.write_u64(self.hash_caculator.as_ref()(lock.as_ref()));
    }
}

impl PartialEq for Object {
    fn eq(&self, other: &Self) -> bool {
        let lock = self.boxed_value.read().unwrap();
        let lock2 = other.boxed_value.read().unwrap();
        self.equality_caculator.as_ref()(lock.as_ref(), lock2.as_ref())
    }
}

impl Eq for Object {

}

impl Display for Object {
    fn fmt(&self, f: &mut Formatter) -> Result {
        let lock = self.boxed_value.read().unwrap();
        write!(f, "{}", self.to_string_caculator.as_ref()(lock.as_ref()))
    }
}

impl Debug for Object {
    fn fmt(&self, f: &mut Formatter) -> Result {
        let lock = self.boxed_value.read().unwrap();
        write!(f, "{}", self.to_string_caculator.as_ref()(lock.as_ref()))
    }
}

unsafe impl Send for Object {

}

unsafe impl Sync for Object {

}

impl Clone for Object {
    fn clone(&self) -> Self {
        let lock = self.boxed_value.read().unwrap();
        let obj = self.clone_maker.as_ref()(lock.as_ref());
        Object {
            boxed_value: RwLock::new(obj),
            hash_caculator: self.hash_caculator.clone(),
            equality_caculator: self.equality_caculator.clone(),
            to_string_caculator: self.to_string_caculator.clone(),
            clone_maker: self.clone_maker.clone()
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;

    #[test]
    fn object() {
        assert_eq!(Object::new("key"), Object::new("key"));
        assert_ne!(Object::new("key"), Object::new("value"));
    }

    #[test]
    fn generic_map() {
        let mut map = HashMap::<Object, Object>::new();
        map.insert(Object::new("key"), Object::new("value"));
        println!("map: {}", map.get(&Object::new("key")).unwrap());
    }

}