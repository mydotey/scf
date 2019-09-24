use std::hash::Hash;

pub mod object;
pub mod immutable;
pub mod mutable;

pub trait KeyConstraints: 'static + Hash + Eq + Send + Sync { }

pub trait ValueConstraints: 'static + Eq + Send + Sync { }

impl<T: 'static + Hash + Eq + Send + Sync> KeyConstraints for T { }

impl<T: 'static + Eq + Send + Sync> ValueConstraints for T { }
