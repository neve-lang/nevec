use super::line::Line;
use super::loc::{Loc, LocBuilder};
use super::note::Note;

#[derive(Clone, Default)]
pub struct Suggestion<'a> {
    header: Option<String>,

    loc: Option<Loc>,

    fix: Option<String>,
    explanation: Option<String>,

    original_line: Option<&'a str>,
    insert: bool,
}

impl<'a> Suggestion<'a> {
    pub fn header(mut self, header: String) -> Self {
        self.header = Some(header);
        self
    }

    pub fn at(mut self, loc: Loc) -> Self {
        self.loc = Some(loc);
        self
    }

    pub fn fix(mut self, fix: String) -> Self {
        self.fix = Some(fix);
        self
    }

    pub fn explanation(mut self, explanation: String) -> Self {
        self.explanation = Some(explanation);
        self
    }

    pub fn original_line(mut self, line: &'a str) -> Self {
        self.original_line = Some(line);
        self
    }

    pub fn insert(mut self) -> Self {
        self.insert = true;
        self
    }

    pub fn build(self) -> Result<Line, SuggestionBuildError> {
        let loc = self.loc.ok_or(SuggestionBuildError)?;
        let fix = self.fix.ok_or(SuggestionBuildError)?;
        let explanation = self.explanation.ok_or(SuggestionBuildError)?;
        let original_line = self.original_line.ok_or(SuggestionBuildError)?;

        let notes = Suggestion::make_notes(&loc, &fix, explanation);
        let line = Suggestion::modify_line(&loc, &fix, original_line, self.insert);

        Ok(Line::builder(loc)
            .header(self.header.ok_or(SuggestionBuildError)?)
            .add(notes)
            .with_line(line)
            .build())
    }

    fn make_notes(loc: &Loc, fix: &str, explanation: String) -> Vec<Note> {
        let loc = LocBuilder::from(loc).len(fix.len()).build();

        vec![Note::fix(loc, explanation)]
    }

    fn modify_line(loc: &Loc, fix: &str, original_line: &'a str, insert: bool) -> String {
        let from = loc.col - 1;
        let to = from + loc.len;

        if insert {
            format!(
                "{}{}{}",
                &original_line[..from],
                fix,
                &original_line[from..]
            )
        } else {
            format!("{}{}{}", &original_line[..from], fix, &original_line[to..])
        }
    }
}

#[derive(Debug)]
pub struct SuggestionBuildError;
