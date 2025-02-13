pub mod line;
pub mod loc;
pub mod msg;
pub mod note;
pub mod report;
pub mod suggestion;

mod write;

use write::color::Color;
pub use write::out::Out;
