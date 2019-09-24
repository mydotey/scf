use std::hash::Hash;
use std::fmt::Display;
use std::any::Any;

pub trait Object: Hash + Eq + Display + Clone {

}

pub type HashCaculator = Fn(&Any) -> u64;
pub type EqualityCaculator = Fn(&Any, &Any) -> bool;
pub type ToStringCaculator = Fn(&Any) -> String;
pub type CloneMaker = Fn(&Any) -> Box::<Any>;
