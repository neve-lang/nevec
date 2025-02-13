use core::fmt;

#[derive(Clone, Debug)]
pub struct Loc {
    pub col: usize,
    pub line: usize,
    pub len: usize,

    pub term_width: usize,
    pub term_col: usize,
}

impl Loc {
    pub fn new(col: usize, line: usize, length: usize) -> Loc {
        Loc {
            col,
            line,
            len: length,

            term_width: length,
            term_col: col,
        }
    }
}

impl Default for Loc {
    fn default() -> Loc {
        Loc::new(0, 1, 0)
    }
}

impl fmt::Display for Loc {
    fn fmt(&self, f: &mut fmt::Formatter) -> Result<(), fmt::Error> {
        write!(f, "{}:{}", self.line, self.col)
    }
}

pub struct LocBuilder {
    col: usize,
    line: usize,
    len: usize,
}

impl LocBuilder {
    pub fn len(mut self, len: usize) -> Self {
        self.len = len;
        self
    }

    pub fn build(self) -> Loc {
        Loc::new(self.col, self.line, self.len)
    }
}

impl From<&Loc> for LocBuilder {
    fn from(loc: &Loc) -> Self {
        Self {
            col: loc.col,
            line: loc.line,
            len: loc.len,
        }
    }
}

impl From<usize> for LocBuilder {
    fn from(u: usize) -> Self {
        Self {
            col: 0,
            line: u,
            len: 0,
        }
    }
}
