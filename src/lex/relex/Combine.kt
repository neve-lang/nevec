package lex.relex

import tok.Tok
import tok.TokKind

class Combine(val left: Tok, val right: Tok) {
    fun into(kind: TokKind): Tok {
        val (lexeme, loc) = left + right
        return Tok(kind, lexeme, loc)
    }
}