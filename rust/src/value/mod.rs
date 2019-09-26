use std::hash::Hash;
use std::fmt::Display;

pub mod any;

mod immutable;
pub use immutable::Immutable;

mod mutable;
pub use mutable::Mutable;

pub trait Value: 'static + Hash + Eq + Display + Clone + Send + Sync { }

impl<T: 'static + Hash + Eq + Display + Clone + Send + Sync> Value for T { }
