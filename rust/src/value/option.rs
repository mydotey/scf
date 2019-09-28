use std::fmt;

pub fn to_string<T: fmt::Display>(o: &Option<T>) -> String {
    match o {
        Some(value) => value.to_string(),
        None => "none".to_string()
    }
}

pub fn to_debug_string<T: fmt::Debug>(o: &Option<T>) -> String {
    match o {
        Some(value) => format!("{:?}", value),
        None => "none".to_string()
    }
}
