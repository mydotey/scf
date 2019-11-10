//! # Simple Configuration Facade
//!  
//! Author: Qiang Zhao <koqizhao@outlook.com>
//! 
//! Github: [https://github.com/mydotey/scf](https://github.com/mydotey/scf)
//! 
//! Usage: [https://github.com/mydotey/scf/tree/master/rust](https://github.com/mydotey/scf/tree/master/rust)
//! 
//! Usage: [https://github.com/mydotey/scf/tree/master/rust/tests](https://github.com/mydotey/scf/tree/master/rust/tests)

#![allow(dead_code)]

#[macro_use]
extern crate lang_extension;

#[macro_use]
extern crate log;

pub mod property;
pub mod source;
pub mod manager;
pub mod facade;

#[cfg(test)]
pub mod tests {
    use std::env::*;
    use std::sync::Once;

    static LOG_INIT: Once = Once::new();

    pub fn init_log() {
        LOG_INIT.call_once(||log4rs::init_file("log4rs.yml", Default::default()).unwrap());
    }
 
    #[test]
    fn it_works() {
        init_log();
        assert_eq!(2 + 2, 4);
        println!("current dir: {:?}", current_dir());
    }
}
