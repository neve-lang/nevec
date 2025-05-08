package parse.tok

import file.span.Loc
import lex.Lex
import tok.Tok
import tok.TokKind

/**
 * Works just like a sliding window for the Parser, keeping track of a [curr] and [prev] token that get updated
 * every [advance] call, using [Lex].
 *
 * @see Lex
 */
class Window(contents: String) {
    private val lex = Lex(contents)

    /**
     * The current [Tok].
     */
    var curr = Tok.eof()

    private var prev = Tok.eof()
    private var prevBeforeNewline = Tok.eof()

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
     * **Advances** if [curr]’s kind matches one of [kinds].
     *
     * @return [curr] if [curr]’s kind matches one of [kinds], `null` otherwise.
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
     * @return [curr]’s [TokKind]. */
    fun kind(): TokKind {
        return curr.kind
    }

    private fun update() {
        prev = curr

        if (!prev.isOf(TokKind.NEWLINE)) {
            prevBeforeNewline = prev
        }

        curr = lex.next()
    }
}