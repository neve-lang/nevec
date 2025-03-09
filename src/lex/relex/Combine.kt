package lex.relex

import tok.Tok
import tok.TokKind

/**
 * Simple helper class to elegantly combine two [Tok]s.
 *
 * @see Relex.merge
 */
class Combine(private val left: Tok, private val right: Tok) {
    fun into(kind: TokKind): Tok {
        val loc = left + right
        val lexeme = loc.lexeme()

        return Tok(kind, lexeme, loc)
    }
}