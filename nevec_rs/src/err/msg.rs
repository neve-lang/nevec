use super::line::{Line, LineBuilder};
use super::loc::Loc;
use super::suggestion::{Suggestion, SuggestionBuildError};

use super::Color;
use super::Out;

#[derive(Clone, Debug)]
pub enum MsgKind {
    Warn,
    Err,
}

pub struct Msg<'a> {
    kind: MsgKind,

    filename: &'a str,
    msg: String,
    loc: Loc,

    lines: Vec<Line>,
}

impl<'a> Msg<'a> {
    pub fn builder<'b>() -> MsgBuilder<'a, 'b> {
        MsgBuilder::default()
    }

    fn cleanup(lines: &[Line]) -> Vec<Line> {
        if lines.is_empty() {
            return Vec::new();
        }

        let first = vec![lines[0].to_owned()];

        let rest = lines
            .windows(2)
            .map(|pair| {
                let a = &pair[0];
                let b = &pair[1];

                if b.line - a.line > 1 {
                    LineBuilder::from(b).showing_previous().build()
                } else {
                    b.to_owned()
                }
            })
            .collect();

        [first, rest].concat()
    }

    pub fn emit(&self, lines: &[&str], out: &mut Out) {
        self.header(out);
        self.locus(out);

        self.lines.iter().for_each(|l| l.emit(lines, out));

        self.finish(out);
    }

    fn header(&self, out: &mut Out) {
        match self.kind {
            MsgKind::Warn => out.painted_in(Color::Yellow, "!  "),
            MsgKind::Err => out.painted_in(Color::Red, "×  "),
        }

        out.str(&self.msg);

        out.newline();
    }

    fn locus(&self, out: &mut Out) {
        out.painted_in(Color::Blue, " ╭─ ");
        out.str(self.filename);

        out.color(Color::Gray);
        out.str(":");

        out.str(&self.loc.to_string());
        out.color(Color::Reset);

        out.newline();
    }

    fn finish(&self, out: &mut Out) {
        out.painted_in(Color::Blue, " ╰─ ");

        out.color(Color::Reset);
        out.newline();
    }
}

#[derive(Default)]
pub struct MsgBuilder<'a, 'b> {
    kind: Option<MsgKind>,

    filename: Option<&'a str>,
    msg: Option<String>,
    loc: Option<Loc>,

    lines: Option<Vec<Line>>,
    suggestions: Option<Vec<Suggestion<'b>>>,
}

impl<'a, 'b> MsgBuilder<'a, 'b> {
    pub fn kind(mut self, kind: MsgKind) -> Self {
        self.kind = Some(kind);
        self
    }

    pub fn filename(mut self, filename: &'a str) -> Self {
        self.filename = Some(filename);
        self
    }

    pub fn msg(mut self, msg: String) -> Self {
        self.msg = Some(msg);
        self
    }

    pub fn loc(mut self, loc: Loc) -> Self {
        self.loc = Some(loc);
        self
    }

    pub fn lines(mut self, lines: Vec<Line>) -> Self {
        self.lines = Some(lines.to_vec());
        self
    }

    pub fn suggestions(mut self, suggestions: Vec<Suggestion<'b>>) -> Self {
        self.suggestions = Some(suggestions.to_vec());
        self
    }

    pub fn build(self) -> Result<Msg<'a>, MsgBuilderError> {
        let suggestions = self.suggestions.unwrap_or_default();

        let Some(lines) = self.lines else {
            return Err(MsgBuilderError);
        };

        let (as_lines, errors): (Vec<Result<Line, _>>, Vec<Result<_, SuggestionBuildError>>) =
            suggestions
                .into_iter()
                .map(Suggestion::build)
                .partition(Result::is_ok);

        if !errors.is_empty() {
            return Err(MsgBuilderError);
        }

        let mut all_lines: Vec<Line> = as_lines.into_iter().map(Result::unwrap).collect();
        all_lines.extend(lines);
        all_lines.sort_by_key(|l| l.line + (l.is_suggestion() as usize));

        let lines = MsgBuilder::cleanup(&all_lines);

        Ok(Msg {
            kind: self.kind.ok_or(MsgBuilderError)?,
            filename: self.filename.ok_or(MsgBuilderError)?,
            msg: self.msg.ok_or(MsgBuilderError)?,
            loc: self.loc.ok_or(MsgBuilderError)?,
            lines,
        })
    }

    fn cleanup(lines: &[Line]) -> Vec<Line> {
        let Some(first) = lines.first() else {
            return Vec::new();
        };

        let first = vec![first.to_owned()];

        let rest = lines
            .windows(2)
            .map(|pair| {
                let a = &pair[0];
                let b = &pair[1];

                if b.line - a.line > 1 {
                    LineBuilder::from(b).showing_previous().build()
                } else {
                    b.to_owned()
                }
            })
            .collect();

        [first, rest].concat()
    }
}

#[derive(Debug)]
pub struct MsgBuilderError;
