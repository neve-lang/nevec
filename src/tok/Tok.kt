package tok

import file.span.Loc

class Tok(val kind: TokKind, private val lexeme: String, val loc: Loc) {
    fun isEof() = kind == TokKind.EOF

    fun isOf(other: TokKind) = kind == other

    operator fun plus(other: Tok) = Pair(lexeme + other.lexeme, (loc + other.loc).build())

    override fun toString() = "\"$lexeme\" ($kind) $loc"
}
