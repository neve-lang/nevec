package parse.tok

import lex.Lex
import tok.Tok
import tok.TokKind

/**
 * Works just like a sliding window for the Parser, keeping track of a [curr] and [prev] token that get updated
 * every [advance] call, using [Lex].
 */
class Window(contents: String) {
    private val lex = Lex(contents)

    var curr = Tok.eof()
    private var prev = Tok.eof()
    private var prevBeforeNewline = Tok.eof()

    fun advance(): Tok {
        update()

        if (curr.isNewline()) {
            return advance()
        }

        return prevBeforeNewline
    }

    fun match(vararg kinds: TokKind): Boolean {
        if (check(*kinds)) {
            advance()
            return true
        }

        return false
    }

    fun check(vararg kinds: TokKind) = kinds.contains(kind())

    fun hadNewline() = prev.isNewline()

    fun isAtEnd() = curr.isEof()

    fun here() = curr.loc

    fun kind() = curr.kind

    private fun update() {
        prev = curr

        if (!prev.isOf(TokKind.NEWLINE)) {
            prevBeforeNewline = prev
        }

        curr = lex.next()
    }
}