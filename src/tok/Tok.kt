package tok

import file.span.Loc

class Tok(val kind: TokKind, private val lexeme: String, val loc: Loc) {
    fun isEof() = kind == TokKind.EOF

    override fun toString(): String {
        return "\"$lexeme\" ($kind) $loc"
    }
}
