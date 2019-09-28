use std::hash::Hash;
use std::fmt::{ Display, Debug };

pub mod any;

pub mod immutable;

pub mod option;

pub trait Object: 'static + Hash + Eq + Clone + Display + Debug { }

impl<T: 'static + Hash + Eq + Clone + Display + Debug> Object for T { }

pub trait ThreadSafe: Send + Sync { }

impl<T: Send + Sync> ThreadSafe for T { }

pub trait ThreadSafeObject: Object + ThreadSafe { }

impl<T: Object + ThreadSafe> ThreadSafeObject for T { }

pub trait TraitObject: 'static + Display + Debug { }

impl<T: 'static + Display + Debug> TraitObject for T { }

pub trait ThreadSafeTraitObject: TraitObject + ThreadSafe { }

impl<T: TraitObject + ThreadSafe> ThreadSafeTraitObject for T { }
