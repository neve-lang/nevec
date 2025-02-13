use std::io::{self, StderrLock, Write as IoWrite};

use supports_color::Stream;

use super::color::Color;

pub struct Out<'a> {
    handle: StderrLock<'a>,
    offset: usize,
}

impl Out<'_> {
    pub fn new(max_line: u32) -> Self {
        let stderr = io::stderr();
        let offset = Out::digits_in(max_line);

        Self {
            handle: stderr.lock(),
            offset,
        }
    }

    fn digits_in(n: u32) -> usize {
        (n.checked_ilog10().unwrap_or(0) + 1) as usize
    }

    pub fn char(&mut self, c: &char) {
        if write!(self.handle, "{}", c).is_err() {
            // TODO: implement more robust error handling, because
            // this is a little silly.
            eprintln!("Failed to write to stderr.");
        }
    }

    pub fn str(&mut self, s: &str) {
        if write!(self.handle, "{}", s).is_err() {
            // TODO: implement more robust error handling, because
            // this is a little silly.
            eprintln!("Failed to write to stderr.");
        }
    }

    pub fn offset(&mut self) {
        self.str(&(" ".repeat(self.offset)));
    }

    pub fn painted_in(&mut self, color: Color, s: &str) {
        self.offset();

        self.color(color);
        self.str(s);
        self.color(Color::Reset);
    }

    pub fn color(&mut self, c: Color) {
        if supports_color::on(Stream::Stderr).is_some() {
            self.str(c.code());
        }
    }

    pub fn newline(&mut self) {
        self.str("\n");
    }
}
