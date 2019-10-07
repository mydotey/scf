
#![allow(dead_code)]

pub mod value;
pub mod property;
pub mod source;
pub mod manager;
pub mod facade;

#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        assert_eq!(2 + 2, 4);
    }
}
