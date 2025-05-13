package parse.ctx

import ctx.Ctx
import err.msg.Msg
import file.span.Loc
import parse.err.ParseErr
import parse.state.ParseState
import parse.tok.Window
import tok.Tok
import tok.TokKind
import type.table.TypeTable

/**
 * Tiny data class intended to be shipped around Parse helpers.
 */
data class ParseCtx(
    val window: Window,
    val typeTable: TypeTable,
    val state: ParseState,
    val cliCtx: Ctx,
) {
    /**
     * Calling this method **advances** the [Window] position.
     *
     * If the previous token (after advancing) is an **error token** ([TokKind.ERR]), a [ParseErr] is reported.
     *
     * @return the previous [Tok].
     *
     * @see tok.stream.TokStream.next
     */
    fun consume(): Tok {
        val prev = window.advance()

        if (prev.isOf(TokKind.ERR)) {
            showMsg(ParseErr.unexpectedChar(prev))
        }

        return prev
    }

    /**
     * Calling this method **advances** the [Window] position.
     *
     * @returns the previous [Tok] if its [kind][TokKind] matches one of the given [expected] kinds, `null` otherwise.
     *
     * When the previous token’s kind does not match any of the token kinds given, a [ParseErr] is reported.
     */
    fun consume(vararg expected: TokKind): Tok? {
        if (!check(*expected)) {
            expect(*expected)
            return null
        }

        return consume()
    }

    /**
     * **Advances** if the current [Tok]’s [kind][TokKind] matches [kind].
     *
     * If that is not the case, an error message is displayed based on the following criteria:
     *
     * - If the current tok’s kind [TokKind.EOF] or a [TokKind.NEWLINE]: we display a [ParseErr.expectedTok] error,
     *   because those tokens are usually associated with empty lexemes, and may look weird to users.
     * - Otherwise, we display a [ParseErr.unexpectedTok] error.
     */
    fun expect(kind: TokKind) {
        if (check(kind)) {
            consume()
            return
        }

        if (check(TokKind.EOF, TokKind.NEWLINE)) {
            state.showMsg(ParseErr.expectedTok(here(), kind))
            return
        }

        state.showMsg(ParseErr.unexpectedTok(curr(), kind))
    }

    /**
     * @return The current [Tok].
     */
    fun curr(): Tok {
        return window.curr
    }

    /**
     * **Advances** if the current token’s kind matches one of [kinds].
     *
     * @return whether the current token’s kind matches one of [kinds].
     */
    fun match(vararg kinds: TokKind): Boolean {
        return window.match(*kinds)
    }

    /**
     * @return whether the current token’s kind matches one of [kinds].
     */
    fun check(vararg kinds: TokKind): Boolean {
        return window.check(*kinds)
    }

    /**
     * @return whether the previous [TokKind] was a newline.
     */
    fun hadNewline(): Boolean {
        return window.hadNewline()
    }

    /**
     * @return whether the current token is an EOF [Tok].
     */
    fun isAtEnd(): Boolean {
        return window.isAtEnd()
    }

    /**
     * @return The current token’s [Loc].
     */
    fun here(): Loc {
        return window.here()
    }

    /**
     * @return The current token’s [TokKind].
     */
    fun kind(): TokKind {
        return window.kind()
    }

    /**
     * @see ParseState.showMsg
     */
    fun showMsg(msg: Msg) {
        state.showMsg(msg)
    }
}