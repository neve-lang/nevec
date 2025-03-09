package tok

import file.span.Loc

/**
 * A source code token.
 *
 * @see TokKind
 */
class Tok(val kind: TokKind, val lexeme: String, val loc: Loc) {
    fun isEof() = kind == TokKind.EOF

    fun isOf(other: TokKind) = kind == other

    operator fun plus(other: Tok) = (loc + other.loc).build()

    override fun toString() = "\"$lexeme\" ($kind) $loc"
}
