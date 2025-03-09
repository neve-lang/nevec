package parse

import err.msg.Msg
import err.note.Note
import err.report.Report
import err.help.Lines
import err.help.Suggest
import file.span.Loc
import lex.lexeme
import tok.Tok
import tok.TokKind

/**
 * Provides helper functions for the most common compiler errors that occur during the parsing phase errors.
 */
object ParseErr {
    fun unexpectedChar(tok: Tok): Msg {
        val msg = tok.lexeme
        val loc = tok.loc

        return Report.err(loc, msg).lines(
            Lines.of(Note.err(loc, "here"))
        ).build()
    }

    fun unexpectedTok(tok: Tok, expected: TokKind): Msg {
        val loc = tok.loc

        val valid = expected.lexeme()
        val got = tok.lexeme

        val displayed = expected.display()

        val suggestion = if (valid != null)
            listOf(Suggest.replacing(at = loc, with = valid))
        else
            emptyList()

        return Report.err(loc, "unexpected token").lines(
            Lines.of(Note.err(loc, "expected $displayed but found $got")) + suggestion
        ).build()
    }

    fun expectedTok(loc: Loc, expected: TokKind): Msg {
        val valid = expected.lexeme()

        val suggestion = if (valid != null)
            listOf(Suggest.adding(valid, at = loc, saying = "however, you can insert it"))
        else
            emptyList()

        return Report.err(loc, "'$valid' was expected, but found nothing").lines(
            Lines.of(Note.err(loc, "expected '$valid'")) + suggestion
        ).build()
    }

    fun expectedExpr(tok: Tok): Msg {
        val loc = tok.loc

        val isAtEnd = tok.isEof()
        val lexeme = tok.display()

        return Report.err(loc, "expected an expression").lines(
            Lines.of(Note.err(loc, "expected an expression, but found '$lexeme'"))
        ).build()
    }

    fun expected(what: String, got: Tok): Msg {
        val loc = got.loc

        return Report.err(loc, "expected $what").lines(
            Lines.of(Note.err(loc, "not $what"))
        ).build()
    }
}

fun Tok.display() = kind.display()

fun TokKind?.display() = if (this != null && this != TokKind.EOF) lexeme() else "end of file"