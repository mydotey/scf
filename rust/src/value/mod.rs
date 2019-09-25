use std::hash::Hash;

pub mod object;

mod immutable;
pub use immutable::Immutable;

mod mutable;
pub use mutable::Mutable;

pub trait KeyConstraints: 'static + Hash + Eq + Send + Sync { }

pub trait ValueConstraints: 'static + Eq + Send + Sync { }

impl<T: 'static + Hash + Eq + Send + Sync> KeyConstraints for T { }

impl<T: 'static + Eq + Send + Sync> ValueConstraints for T { }
