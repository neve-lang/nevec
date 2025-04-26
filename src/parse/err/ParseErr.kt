package parse.err

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
 *
 * @see Msg
 */
object ParseErr {
    /**
     * @param tok The unexpected character error [Tok].
     *
     * @return an error message for unexpected character [Toks][Tok].
     */
    fun unexpectedChar(tok: Tok): Msg {
        val msg = tok.lexeme
        val loc = tok.loc

        return Report.err(loc, msg).lines(
            Lines.of(Note.err(loc, "here"))
        ).build()
    }

    /**
     * @param tok The unexpected [Tok] in question.
     *
     * @param expected The actual expected [TokKind].
     *
     * @return an error message for unexpected [Toks][Tok].
     */
    fun unexpectedTok(tok: Tok, expected: TokKind): Msg {
        val loc = tok.loc

        val valid = expected.lexeme()
        val got = tok.lexeme

        val displayed: String = expected.display()

        val suggestion = if (valid != null)
            listOf(Suggest.replacing(at = loc, with = valid))
        else
            emptyList()

        return Report.err(loc, "unexpected token").lines(
            Lines.of(Note.err(loc, "expected $displayed, but found $got")) + suggestion
        ).build()
    }

    /**
     * @param at The location where the [Tok] didn’t match.
     *
     * @param expected The actual expected [TokKind].
     *
     * @return an error message for cases where the [Tok] found doesn’t match the expected one.
     */
    fun expectedTok(at: Loc, expected: TokKind): Msg {
        val valid = expected.lexeme()

        val suggestion = if (valid != null)
            listOf(Suggest.adding(valid, at, saying = "however, you can insert it"))
        else
            emptyList()

        return Report.err(at, "'$valid' was expected, but found nothing").lines(
            Lines.of(Note.err(at, "expected '$valid'")) + suggestion
        ).build()
    }

    /**
     * @param tok The token where the expression was expected.
     *
     * @return an error message for unexpected tokens in places where an expression was expected.
     */
    fun expectedExpr(tok: Tok): Msg {
        val loc = tok.loc
        val lexeme = tok.display()

        return Report.err(loc, "expected an expression").lines(
            Lines.of(Note.err(loc, "expected an expression, but found '$lexeme'"))
        ).build()
    }
}

/**
 * @return
 */
fun Tok.display(): String  {
    return kind.display()
}

fun TokKind?.display() = this?.lexeme() ?: "end of file"