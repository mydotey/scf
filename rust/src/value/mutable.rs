use std::hash::{ Hash, Hasher };
use std::any::Any;
use std::collections::hash_map::DefaultHasher;
use std::fmt::{ Display, Formatter, Result, Debug };
use std::rc::Rc;

use super::object::*;

pub struct Mutable {
    value: Box<Any>,
    hash_caculator: Rc<Box<HashCaculator>>,
    equality_caculator: Rc<Box<EqualityCaculator>>,
    to_string_caculator: Rc<Box<ToStringCaculator>>,
    clone_maker: Rc<Box<CloneMaker>>
}

impl Mutable {
    pub fn new<T: 'static + Hash + Eq + Display + Clone>(value: T) -> Mutable {
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

        Mutable {
            value,
            hash_caculator: Rc::new(Box::new(hash_caculator)),
            equality_caculator: Rc::new(Box::new(equality_caculator)),
            to_string_caculator: Rc::new(Box::new(to_string_caculator)),
            clone_maker: Rc::new(Box::new(clone_maker))
        }
    }

    pub fn value(self) -> Box<Any> {
        self.value
    }

}

impl Hash for Mutable {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.hash_caculator.as_ref()(self.value.as_ref()));
    }
}

impl PartialEq for Mutable {
    fn eq(&self, other: &Self) -> bool {
        self.equality_caculator.as_ref()(self.value.as_ref(), other.value.as_ref())
    }
}

impl Eq for Mutable {

}

impl Display for Mutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.value.as_ref()))
    }
}

impl Debug for Mutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.value.as_ref()))
    }
}

unsafe impl Send for Mutable {

}

unsafe impl Sync for Mutable {

}

impl Object for Mutable {

}

impl Clone for Mutable {
    fn clone(&self) -> Self {
        Mutable {
            value: self.clone_maker.as_ref()(self.value.as_ref()),
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
        assert_eq!(Mutable::new("key"), Mutable::new("key"));
        assert_ne!(Mutable::new("key"), Mutable::new("value"));
    }

    #[test]
    fn generic_map() {
        let mut map = HashMap::<Mutable, Mutable>::new();
        map.insert(Mutable::new("key"), Mutable::new("value"));
        println!("map: {}", map.get(&Mutable::new("key")).unwrap());
    }

}