use super::loc::Loc;
use super::msg::{Msg, MsgBuilder, MsgKind};

use super::Out;

pub struct Report<'a> {
    filename: String,
    lines: Vec<&'a str>,
}

impl<'a> Report<'a> {
    pub fn new(filename: String, lines: impl Into<Vec<&'a str>>) -> Self {
        Self {
            filename,
            lines: lines.into(),
        }
    }

    pub fn err(&self, loc: Loc, msg: String) -> MsgBuilder {
        self.with_kind(MsgKind::Err, loc, msg)
    }

    pub fn warn(&self, loc: Loc, msg: String) -> MsgBuilder {
        self.with_kind(MsgKind::Warn, loc, msg)
    }

    fn with_kind(&self, kind: MsgKind, loc: Loc, msg: String) -> MsgBuilder {
        Msg::builder()
            .kind(kind)
            .filename(&self.filename)
            .loc(loc)
            .msg(msg)
    }
}

pub trait PrintMsg {
    fn print(self, report: &Report, out: &mut Out);
}

impl PrintMsg for Msg<'_> {
    fn print(self, report: &Report, out: &mut Out) {
        self.emit(&report.lines, out);
    }
}
