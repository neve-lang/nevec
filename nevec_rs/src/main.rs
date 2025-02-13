mod cli;
mod err;

use err::line::Line;
use err::loc::Loc;
use err::note::Note;
use err::report::{PrintMsg, Report};
use err::suggestion::Suggestion;
use err::Out;

fn main() {
    let (_filename, _options) = cli::parse_args();

    let lines = vec!["fun main", "  let x = not 10", "end"];

    let loc_a = Loc::new(4, 2, 4);
    let loc_b = Loc::new(5, 1, 4);
    let loc_c = Loc::new(1, 3, 3);

    let notes_a = vec![
        Note::harmless(Loc::new(3, 2, 5), "declaring x".to_string()),
        Note::fix(Loc::new(15, 2, 2), "make this a bool".to_string()),
        Note::err(Loc::new(11, 2, 3), "type mismatch".to_string()),
    ];

    let notes_b = vec![Note::harmless(
        loc_b.clone(),
        "function begins here".to_string(),
    )];

    let notes_c = vec![Note::harmless(
        loc_c.clone(),
        "function ends here".to_string(),
    )];

    let line_a = Line::builder(loc_a.clone())
        .add(notes_a)
        .header("silly made-up error msg".to_string())
        .build();

    let suggestion = Suggestion::default()
        .at(Loc::new(15, 2, 2))
        .header("you can make it a Bool".to_string())
        .explanation("converts to Bool".to_string())
        .fix("10.bool".to_string())
        .original_line("  let x = not 10");

    let line_b = Line::builder(loc_b).add(notes_b).build();
    let line_c = Line::builder(loc_c).add(notes_c).build();

    let mut write = Out::new(3);

    let report = Report::new("test.neve".to_string(), lines);

    let err = report
        .err(loc_a, "silly little mistake".to_string())
        .lines(vec![line_a, line_b, line_c])
        .suggestions(vec![suggestion])
        .build()
        .expect("how did this happen?");

    err.print(&report, &mut write);
}
