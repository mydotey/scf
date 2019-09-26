use std::hash::{ Hash, Hasher };
use std::collections::hash_map::DefaultHasher;
use std::fmt::Display;
use std::any::Any;
use std::ops::Deref;
use super::Value;

pub trait AnyValue: Value + Deref + AsRef<Any> + Into<Box<Any>> {

}

impl<T: Value + Deref + AsRef<Any> + Into<Box<Any>>> AnyValue for T { }

pub type HashCaculator = Fn(&Any) -> u64;
pub type EqualityCaculator = Fn(&Any, &Any) -> bool;
pub type ToStringCaculator = Fn(&Any) -> String;
pub type CloneMaker = Fn(&Any) -> Box::<Any>;

pub fn hash<T: 'static + Hash>(me: &Any) -> u64 {
    let mut hasher = DefaultHasher::default();
    me.downcast_ref::<T>().unwrap().hash(&mut hasher);
    hasher.finish()
}

pub fn equal<T: 'static + Eq + Display>(me: &Any, other: &Any) -> bool {
    let (me, other) = (me.downcast_ref::<T>(), other.downcast_ref::<T>());
    match me {
        Some(r) => {
            match other {
                Some(r2) => {
                    *r == *r2
                },
                None => false
            }
        },
        None => false
    }
}

pub fn to_string<T: 'static + Display>(me: &Any) -> String {
    match me.downcast_ref::<T>() {
        Some(r) => format!("{}", r),
        None => format!("{:?}", me)
    }
}

pub fn clone<T: 'static + Clone>(me: &Any) -> Box<Any> {
    Box::new(me.downcast_ref::<T>().unwrap().clone())
}
