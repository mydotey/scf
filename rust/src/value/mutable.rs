use std::hash::{ Hash, Hasher };
use std::any::Any;
use std::fmt::{ Display, Formatter, Result, Debug };
use std::rc::Rc;
use std::ops::{ Deref, DerefMut };

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
        Mutable {
            value: Box::new(value),
            hash_caculator: Rc::new(Box::new(hash::<T>)),
            equality_caculator: Rc::new(Box::new(equal::<T>)),
            to_string_caculator: Rc::new(Box::new(to_string::<T>)),
            clone_maker: Rc::new(Box::new(clone::<T>))
        }
    }
}

impl Hash for Mutable {
    fn hash<H: Hasher>(&self, state: &mut H) {
        state.write_u64(self.hash_caculator.as_ref()(self.as_ref()));
    }
}

impl PartialEq for Mutable {
    fn eq(&self, other: &Self) -> bool {
        self.equality_caculator.as_ref()(self.as_ref(), other.as_ref())
    }
}

impl Eq for Mutable {

}

impl Display for Mutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.as_ref()))
    }
}

impl Debug for Mutable {
    fn fmt(&self, f: &mut Formatter) -> Result {
        write!(f, "{}", self.to_string_caculator.as_ref()(self.as_ref()))
    }
}

impl Into<Box<Any>> for Mutable {

    fn into(self) -> Box<Any> {
        self.value
    }

}

impl Deref for Mutable {
    type Target = Any;

    fn deref(&self) -> &Self::Target {
        self.as_ref()
    }
}

impl DerefMut for Mutable {
    fn deref_mut(&mut self) -> &mut Self::Target {
        self.value.as_mut()
    }
}

impl AsRef<Any> for Mutable {

    fn as_ref(&self) -> &Any {
        self.value.as_ref()
    }

}

impl AsMut<Any> for Mutable {

    fn as_mut(&mut self) -> &mut Any {
        self.value.as_mut()
    }

}

impl Clone for Mutable {
    fn clone(&self) -> Self {
        Mutable {
            value: self.clone_maker.as_ref()(self.as_ref()),
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