use std::iter;
use std::str::Chars;

use super::Color;
use super::Out;

use super::loc::{Loc, LocBuilder};
use super::note::Note;

#[derive(Clone)]
struct ColorPair {
    pub color: Color,

    pub from: usize,
    pub to: usize,
}

impl From<&Note> for ColorPair {
    fn from(note: &Note) -> ColorPair {
        let color = note.color();

        let from = note.loc.col;
        let to = from + note.loc.len;

        ColorPair { color, from, to }
    }
}

struct ColorPairs<'a> {
    pairs: Vec<ColorPair>,

    chars: Chars<'a>,
}

impl ColorPairs<'_> {
    fn new(pairs: impl Into<Vec<ColorPair>>, line: &str) -> ColorPairs {
        let pairs = pairs.into();
        let chars = line.chars();

        ColorPairs { pairs, chars }
    }

    fn color(&mut self, out: &mut Out) {
        let froms = self.pairs.iter().map(|p| p.from).collect::<Vec<usize>>();
        let tos = self.pairs.iter().map(|p| p.to).collect::<Vec<usize>>();

        self.color_chars(&froms, &tos, out);
    }

    fn color_chars(&mut self, froms: &[usize], tos: &[usize], out: &mut Out) {
        for (col, c) in self.chars.by_ref().enumerate() {
            if let Some((index, ..)) = froms.iter().enumerate().find(|(.., &f)| f - 1 == col) {
                let pair = &self.pairs[index];
                out.color(pair.color);
            }

            if tos.iter().any(|&t| t - 1 == col) {
                out.color(Color::Reset);
            }

            out.char(&c);
        }
    }
}

#[derive(Clone)]
pub struct Line {
    notes: Vec<Note>,

    pub line: usize,
    header_msg: Option<String>,
    show_previous_line: bool,

    modified_line: Option<String>,
}

impl Line {
    pub fn builder(loc: Loc) -> LineBuilder {
        LineBuilder {
            notes: Vec::new(),

            loc,
            header_msg: None,
            show_previous_line: false,
            modified_line: None,
        }
    }

    pub fn is_suggestion(&self) -> bool {
        self.modified_line.is_some()
    }

    pub fn emit(&self, lines: &[&str], out: &mut Out) {
        // we *could* just make `self` `mut`, but because most `Line`s will
        // be declared *inline*, i think it’s better to keep things that way,
        // so that we don’t always need a let binding in the middle.
        // this might change, though, if let bindings become absolutely
        // necessary.
        let mut notes = self.notes.to_vec();
        notes.sort_by_key(|n| n.loc.col);

        let mut color_pairs = self.color_pairs(&notes, lines, self.modified_line.as_ref());

        self.out_header(out);

        self.display(lines, &mut color_pairs, out);
        self.out_notes(&notes, out);

        self.finish(out);
    }

    fn color_pairs<'b, 'a: 'b>(
        &self,
        notes: &[Note],
        lines: &'a [&'a str],
        modified_line: Option<&'b String>,
    ) -> ColorPairs<'b> {
        let line = self.line(lines, modified_line);

        ColorPairs::new(
            notes
                .iter()
                .map(ColorPair::from)
                .collect::<Vec<ColorPair>>(),
            line,
        )
    }

    fn out_header(&self, out: &mut Out) {
        if let Some(msg) = &self.header_msg {
            out.painted_in(Color::Blue, " ├─ ");
            out.str(msg);

            out.newline();
        }
    }

    fn display(&self, lines: &[&str], color_pairs: &mut ColorPairs, out: &mut Out) {
        if self.show_previous_line {
            self.display_previous(lines, out);
        }

        let line_number = self.line;
        self.line_locus(line_number, out);

        color_pairs.color(out);
        out.newline();
    }

    fn display_previous(&self, lines: &[&str], out: &mut Out) {
        if self.line == 1 {
            return;
        }

        let previous_line_number = self.line - 1;
        let index = previous_line_number - 1;
        let previous_line = lines[index];

        self.line_locus(previous_line_number, out);

        out.color(Color::Gray);
        out.str(previous_line);
        out.newline();
    }

    fn out_notes(&self, notes: &[Note], out: &mut Out) {
        self.underlines(notes, out);
        self.hangs(notes, out);
    }

    fn underlines(&self, notes: &[Note], out: &mut Out) {
        self.empty_line_locus(out);

        notes
            .iter()
            .zip(iter::once(1).chain(notes.iter().map(Note::until)))
            .for_each(|(n, col)| n.underline(col, out));

        out.newline();
    }

    fn hangs(&self, notes: &[Note], out: &mut Out) {
        let mut notes = notes;

        while !notes.is_empty() {
            self.empty_line_locus(out);
            self.each_hang(notes, out);

            out.newline();

            let until = notes.len() - 1;
            notes = &notes[..until];
        }
    }

    fn each_hang(&self, notes: &[Note], out: &mut Out) {
        let mut notes = notes;

        let Some(last_note) = &notes.last() else {
            return;
        };

        for col in 1..last_note.end() {
            let Some(head) = &notes.first() else {
                break;
            };

            if col != head.hang {
                out.str(" ");
                continue;
            }

            let s = if notes.len() == 1 {
                format!("╰─ {}", head.msg)
            } else {
                String::from("│")
            };

            out.color(head.color());
            out.str(&s);

            notes = &notes[1..];
        }
    }

    fn finish(&self, out: &mut Out) {
        out.color(Color::Reset);
    }

    fn line<'a>(&self, lines: &[&'a str], modified_line: Option<&'a String>) -> &'a str {
        modified_line.map_or(lines[self.line - 1], |v| v)
    }

    fn line_locus(&self, line_number: usize, out: &mut Out) {
        out.color(Color::Gray);
        out.str(&line_number.to_string());

        out.color(Color::Blue);
        out.str(" │ ");

        out.color(Color::Reset);
    }

    fn empty_line_locus(&self, out: &mut Out) {
        out.painted_in(Color::Blue, " · ");
    }
}

#[derive(Default)]
pub struct LineBuilder {
    notes: Vec<Note>,

    loc: Loc,
    header_msg: Option<String>,
    show_previous_line: bool,

    modified_line: Option<String>,
}

impl LineBuilder {
    pub fn add(mut self, notes: impl Into<Vec<Note>>) -> Self {
        self.notes = notes.into();
        self
    }

    pub fn header(mut self, msg: String) -> Self {
        self.header_msg = Some(msg);
        self
    }

    pub fn showing_previous(mut self) -> Self {
        self.show_previous_line = true;
        self
    }

    pub fn with_line(mut self, modified_line: String) -> Self {
        self.modified_line = Some(modified_line);
        self
    }

    pub fn build(self) -> Line {
        Line {
            notes: self.notes,

            line: self.loc.line,
            header_msg: self.header_msg,
            show_previous_line: self.show_previous_line,

            modified_line: self.modified_line,
        }
    }
}

impl From<&Line> for LineBuilder {
    fn from(line: &Line) -> Self {
        Self {
            notes: line.notes.clone(),

            loc: LocBuilder::from(line.line).build(),
            header_msg: line.header_msg.clone(),
            show_previous_line: line.show_previous_line,

            modified_line: line.modified_line.clone(),
        }
    }
}
