package parse.tok

import file.span.Loc
import lex.Lex
import tok.Tok
import tok.TokKind
import tok.stream.TokStream

/**
 * Works just like a sliding window for the Parser, keeping track of a [curr] and [prev] token that get updated
 * every [advance] call.
 *
 * @see Lex
 */
class Window(private val stream: TokStream) {
    companion object {
        /**
         * @return A new [Window] based on the source code, building a [TokStream] using [Lex].
         */
        fun from(contents: String): Window {
            return Lex(contents).toks().let(::Window)
        }
    }

    /**
     * The current [Tok].
     */
    var curr = Tok.eof()

    /**
     * Updates the current [Tok] and returns the previous one.  It skips [TokKind.NEWLINE] tokens
     * by recursively calling [advance] when it encounters one.
     */
    fun advance(): Tok {
        update()

        if (curr.isNewline()) {
            return advance()
        }

        return prevBeforeNewline
    }

    /**
     * Advances for as long as the [closing] token [kind][TokKind] is not matched, or as long as the end of the
     * [TokStream][tok.stream.TokStream] is not reached.
     *
     * If an [opening] token kind is matched, we increment the “depth,” effectively skipping nesting.
     *
     * An example of where the aforementioned behavior may be useful could be:
     *
     * ```
     * -- we want to ensure that we skip until the “correct” closing token
     * @[type == [K: V]]
     * --              ^ so this token
     * ```
     */
   fun skipToClosing(closing: TokKind, opening: TokKind, depth: Int = 1) {
        if (depth == 0 || isAtEnd()) {
            return
        }

        val newDepth = when (kind()) {
            closing -> depth - 1
            opening -> depth + 1
            else -> depth
        }

        advance()

        return skipToClosing(closing, opening, newDepth)
   }

    /**
     * **Advances** if [curr]’s kind matches one of [kinds].
     *
     * @return [curr] if [curr]’s kind matches one of [kinds].
     */
    fun take(vararg kinds: TokKind): Tok? {
        return if (match(*kinds))
            prevBeforeNewline
        else
            null
    }

    /**
     * **Advances** if [curr]’s kind matches one of [kinds].
     *
     * @return whether [curr]’s kind matches one of [kinds].
     */
    fun match(vararg kinds: TokKind): Boolean {
        if (check(*kinds)) {
            advance()
            return true
        }

        return false
    }

    /**
     * @return whether [curr]’s kind matches one of [kinds].
     */
    fun check(vararg kinds: TokKind): Boolean {
        return kinds.contains(kind())
    }

    /**
     * @return whether the previous [TokKind] was a newline.
     */
    fun hadNewline(): Boolean {
        return prev.isNewline()
    }

    /**
     * @return whether [curr] is an EOF [Tok].
     */
    fun isAtEnd(): Boolean {
        return curr.isEof()
    }

    /**
     * @return [curr]’s [Loc].
     */
    fun here(): Loc {
        return curr.loc
    }

    /**
     * @return [curr]’s [TokKind].
     */
    fun kind(): TokKind {
        return curr.kind
    }

    private var prev = Tok.eof()
    private var prevBeforeNewline = Tok.eof()


    private fun update() {
        prev = curr

        if (!prev.isOf(TokKind.NEWLINE)) {
            prevBeforeNewline = prev
        }

        curr = stream.next()
    }
}