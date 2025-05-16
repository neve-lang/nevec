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
import java.util.Stack

/**
 * Tiny data class intended to be shipped around Parse helpers.
 *
 * @property window The sliding window used to keep track of tokens.
 * @property states A stack of [ParseState], intended to have a new [ParseState] pushed to the top whenever
 * [ParseCtx] is shipped to some other parse module.
 * @property cliCtx The compiler context, including CLI options and more.
 */
data class ParseCtx(
    val window: Window,
    val typeTable: TypeTable,
    val states: Stack<ParseState>,
    val cliCtx: Ctx,
) {
    companion object {
        /**
         * @return A new [ParseCtx] from [contents] and a given [Ctx].
         */
        fun from(contents: String, ctx: Ctx): ParseCtx {
            return ParseCtx(
                Window.from(contents),
                TypeTable(),
                Stack<ParseState>().apply { add(ParseState()) },
                ctx
            )
        }
    }

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
     * @return the previous [Tok] if its [kind][TokKind] matches one of the given [expected] kinds, `null` otherwise.
     *
     * When the previous token’s kind does not match any of the token kinds given, a [ParseErr] is reported.
     */
    fun consume(expected: TokKind): Tok? {
        if (!check(expected)) {
            expect(expected)
            return null
        }

        return consume()
    }

    /**
     * Advances for as long as the [closing] token [kind][TokKind] is not matched, or as long as the end of the
     * [TokStream][tok.stream.TokStream] is not reached.
     *
     * @see Window.skipToClosing
     */
    fun skipToClosing(opening: TokKind, closing: TokKind) {
        window.skipToClosing(opening, closing)
    }

    /**
     * This method call **advances** the [Window] position.
     *
     * @return The previous [Tok]’s (after advancing) position.
     */
    fun consumeHere(): Loc {
        return here().also { window.advance() }
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
            state().showMsg(ParseErr.expectedTok(here(), kind))
            return
        }

        state().showMsg(ParseErr.unexpectedTok(curr(), kind))
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
     * @return The top of the [states] stack.
     */
    fun state(): ParseState {
        return states.first()
    }

    /**
     * @return A new [ParseCtx] with a fresh [ParseState] at the top of the [states] stack.
     */
    fun new(): ParseCtx {
        return ParseCtx(
            window,
            typeTable,
            states.apply { add(ParseState()) },
            cliCtx
        )
    }

    /**
     * @return The current [ParseCtx] with the last element popped out of it.
     */
    fun popped(): ParseCtx {
        return ParseCtx(
            window,
            typeTable,
            states.apply { pop() },
            cliCtx
        )
    }

    /**
     * @see ParseState.showMsg
     */
    fun showMsg(msg: Msg) {
        state().showMsg(msg)
    }
}