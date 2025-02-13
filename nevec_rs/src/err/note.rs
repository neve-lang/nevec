use super::loc::Loc;
use super::Color;
use super::Out;

#[derive(Clone, Debug)]
pub enum NoteKind {
    Harmless,
    Err,
    Fix,
}

#[derive(Clone, Debug)]
pub struct Note {
    kind: NoteKind,
    pub loc: Loc,
    pub msg: String,

    len: usize,
    pub hang: usize,
}

impl NoteKind {
    pub fn color(&self) -> Color {
        match self {
            NoteKind::Harmless => Color::Blue,
            NoteKind::Err => Color::Red,
            NoteKind::Fix => Color::Green,
        }
    }
}

impl Note {
    fn new(kind: NoteKind, loc: Loc, msg: String) -> Note {
        let center = loc.term_width / 2;
        let len = loc.term_col + loc.term_width - 1;
        let hang = loc.term_col + center;

        Note {
            kind,
            loc,
            msg,

            len,
            hang,
        }
    }

    pub fn harmless(loc: Loc, msg: String) -> Note {
        Note::new(NoteKind::Harmless, loc, msg)
    }

    pub fn err(loc: Loc, msg: String) -> Note {
        Note::new(NoteKind::Err, loc, msg)
    }

    pub fn fix(loc: Loc, msg: String) -> Note {
        Note::new(NoteKind::Fix, loc, msg)
    }

    fn next(&self, col: usize) -> &str {
        let loc = &self.loc;

        let begin = loc.term_col;
        let end = self.len;

        if col == self.hang {
            "┬"
        } else if col >= begin && col <= end {
            "─"
        } else {
            " "
        }
    }

    pub fn underline(&self, col: usize, out: &mut Out) {
        for (curr, col) in (0..).zip(col..=self.len) {
            if curr == 0 {
                out.color(self.kind.color());
            }

            let c = self.next(col);

            out.str(c);
        }

        out.color(Color::Reset);
    }

    pub fn color(&self) -> Color {
        self.kind.color()
    }

    pub fn end(&self) -> usize {
        self.loc.col + self.loc.len
    }

    pub fn until(&self) -> usize {
        self.len + 1
    }
}
