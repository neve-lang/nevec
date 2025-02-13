#[derive(Clone, Copy)]
pub enum Color {
    Red,
    Green,
    Blue,
    Yellow,
    Gray,
    Reset,
}

impl Color {
    pub fn code(&self) -> &str {
        match self {
            Color::Red => "\x1b[31m",
            Color::Green => "\x1b[32m",
            Color::Blue => "\x1b[34m",
            Color::Yellow => "\x1b[33m",
            Color::Gray => "\x1b[2m",
            Color::Reset => "\x1b[0m",
        }
    }
}
