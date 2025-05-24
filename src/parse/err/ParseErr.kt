package parse.err

import err.msg.Msg
import err.note.Note
import err.report.Report
import err.help.Lines
import err.help.Suggest
import file.span.Loc
import lex.lexeme
import meta.fail.MetaFail
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

        val displayed = expected.lexeme()

        val msg = if (displayed != null)
            "expected $expected, but found $got"
        else
            "here"

        val suggestion = if (valid != null)
            listOf(Suggest.replacing(at = loc, with = valid))
        else
            emptyList()

        return Report.err(loc, "unexpected token").lines(
            Lines.of(Note.err(loc, msg)) + suggestion
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
     * @return An error message for unexpected tokens in places where an expression was expected.
     */
    fun expectedExpr(tok: Tok): Msg {
        val loc = tok.loc
        val lexeme = tok.lexeme

        return Report.err(loc, "expected an expression").lines(
            Lines.of(Note.err(loc, "expected an expression, but found '$lexeme'"))
        ).build()
    }

    /**
     * @return An error message for a situation where the user tries to use a feature that was not enabled by some
     * CLI option.
     *
     * @param feature The full name of the feature, i.e. `"meta assertions"`.
     * @param arg The command line argument associated with it.  For [meta assertions][meta.comp.asserts.MetaAssert],
     * this would be `--meta-asserts`.
     * @param loc The [Loc] of the error.
     */
    fun notEnabled(feature: String, arg: String, loc: Loc): Msg {
        return Report.err(loc, "$feature not enabled").lines(
            Lines.single(
                Note.err(loc, "not enabled"),
                header = "run `nevec` with `$arg` to enable them"
            )
        ).build()
    }

    /**
     * @return An error message for a situation where the Parser expected a top-level declaration, but something
     * else was found.
     */
    fun expectedTopDecl(loc: Loc): Msg {
        return Report.err(loc, "expected a top-level declaration").lines(
            Lines.of(Note.err(loc, "here"))
        ).build()
    }

    /**
     * @param loc The [Loc] of the node where the meta fail occurred.
     * @param fail The [MetaFail] in question.
     *
     * @return An error message depending on the kind of [MetaFail], or `null` if it is an [Input][MetaFail.Input] fail,
     * as we want to avoid reporting the same error twice, or a [Dummy][MetaFail.Dummy] fail.
     *
     * @see meta.result.MetaResult
     * @see MetaFail
     */
    fun metaFail(loc: Loc, fail: MetaFail) = when (fail) {
        is MetaFail.Target -> metaTargetFail(loc, fail)
        is MetaFail.Parse -> metaParseFail(fail)
        is MetaFail.Name -> metaNameFail(fail)
        is MetaFail.NotEnabled -> metaNotEnabledFail(fail)
        is MetaFail.Input -> null
        is MetaFail.Dummy -> null
    }

    private fun metaTargetFail(loc: Loc, fail: MetaFail.Target): Msg {
        return Report.err(fail.loc, "meta component target mismatch").lines(
            Lines.of(
                Note.info(loc, "this"),
                Note.err(fail.loc, "does not apply to this"),
            )
        ).build()
    }

    private fun metaParseFail(fail: MetaFail.Parse): Msg {
        return Report.err(fail.loc, "unexpected meta component syntax").lines(
            Lines.of(Note.err(fail.loc, "here"))
        ).build()
    }

    private fun metaNameFail(fail: MetaFail.Name): Msg {
        return Report.err(fail.loc, "unexpected meta component name").lines(
            Lines.of(
                Note.err(fail.loc, "unknown meta component ‘${fail.name}’")
            )
        ).build()
    }

    private fun metaNotEnabledFail(fail: MetaFail.NotEnabled): Msg {
        return notEnabled(feature = "meta assertions", arg = "--meta-assert", fail.loc)
    }
}