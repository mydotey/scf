use std::hash::{ Hash, Hasher };
use std::collections::hash_map::DefaultHasher;
use std::fmt::{ Display, Debug, Formatter, Result };
use std::any::Any;
use std::rc::Rc;
use std::ops::{ Deref, DerefMut };

use super::Object;

pub type HashCaculator = Fn(&Any) -> u64;
pub type EqualityCaculator = Fn(&Any, &Any) -> bool;
pub type ToStringCaculator = Fn(&Any) -> String;
pub type CloneMaker = Fn(&Any) -> Box<Any>;

pub fn hash<T: 'static + Hash>(me: &Any) -> u64 {
    let mut hasher = DefaultHasher::default();
    me.downcast_ref::<T>().unwrap().hash(&mut hasher);
    hasher.finish()
}

pub fn equal<T: 'static + Eq + Display>(me: &Any, other: &Any) -> bool {
    let (me, other) = (me.downcast_ref::<T>(), other.downcast_ref::<T>());
    *me.unwrap() == *other.unwrap()
}

pub fn to_string<T: 'static + Display>(me: &Any) -> String {
    format!("{}", me.downcast_ref::<T>().unwrap())
}

pub fn to_debug_string<T: 'static + Debug>(me: &Any) -> String {
    format!("{:?}", me.downcast_ref::<T>().unwrap())
}

pub fn clone<T: 'static + Clone>(any: &Any) -> Box<Any> {
    Box::new(any.downcast_ref::<T>().unwrap().clone())
}

pub struct AnyObject {
    value: Box<Any>,
    hash_caculator: Rc<Box<HashCaculator>>,
    equality_caculator: Rc<Box<EqualityCaculator>>,
    to_string_caculator: Rc<Box<ToStringCaculator>>,
    to_debug_string_caculator: Rc<Box<ToStringCaculator>>,
    clone_maker: Rc<Box<CloneMaker>>
}

impl AnyObject {
    pub fn new<T: Object>(value: T) -> AnyObject {
        AnyObject {
            value: Box::new(value),
            hash_caculator: Rc::new(Box::new(hash::<T>)),
            equality_caculator: Rc::new(Box::new(equal::<T>)),
            to_string_caculator: Rc::new(Box::new(to_string::<T>)),
            to_debug_string_caculator: Rc::new(Box::new(to_string::<T>)),
            clone_maker: Rc::new(Box::new(clone::<T>))
        }
    }
}

impl Hash for AnyObject {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.hash_caculator.as_ref()(self.as_ref()));
    }
}

impl PartialEq for AnyObject {
    fn eq(&self, other: &Self) -> bool {
        self.equality_caculator.as_ref()(self.as_ref(), other.as_ref())
    }
}

impl Eq for AnyObject {

}

impl Clone for AnyObject {
    fn clone(&self) -> Self {
        AnyObject {
            value: self.clone_maker.as_ref()(self.as_ref()),
            hash_caculator: self.hash_caculator.clone(),
            equality_caculator: self.equality_caculator.clone(),
            to_string_caculator: self.to_string_caculator.clone(),
            to_debug_string_caculator: self.to_debug_string_caculator.clone(),
            clone_maker: self.clone_maker.clone()
        }
    }
}

impl Display for AnyObject {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.as_ref()))
    }
}

impl Debug for AnyObject {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_debug_string_caculator.as_ref()(self.as_ref()))
    }
}

impl Into<Box<Any>> for AnyObject {

    fn into(self) -> Box<Any> {
        self.value
    }

}

impl Deref for AnyObject {
    type Target = Any;

    fn deref(&self) -> &Self::Target {
        self.as_ref()
    }
}

impl DerefMut for AnyObject {
    fn deref_mut(&mut self) -> &mut Self::Target {
        self.value.as_mut()
    }
}

impl AsRef<Any> for AnyObject {

    fn as_ref(&self) -> &Any {
        self.value.as_ref()
    }

}

impl AsMut<Any> for AnyObject {

    fn as_mut(&mut self) -> &mut Any {
        self.value.as_mut()
    }

}

#[cfg(test)]
mod tests {
    use super::*;
    use std::collections::HashMap;

    #[test]
    fn object() {
        assert_eq!(AnyObject::new("key"), AnyObject::new("key"));
        assert_ne!(AnyObject::new("key"), AnyObject::new("value"));
    }

    #[test]
    fn generic_map() {
        let mut map = HashMap::<AnyObject, AnyObject>::new();
        map.insert(AnyObject::new("key"), AnyObject::new("value"));
        println!("map: {}", map.get(&AnyObject::new("key")).unwrap());
    }

}