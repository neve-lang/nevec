package tok

import file.span.Loc

/**
 * A source code token.
 *
 * @see TokKind
 */
class Tok(val kind: TokKind, val lexeme: String, val loc: Loc) {
    companion object {
        /**
         * @return A [Tok] with `TokKind.EOF`, an empty [lexeme] and whose [Loc] is [Loc.new].
         */
        fun eof(): Tok {
            return Tok(TokKind.EOF, "", Loc.new())
        }
    }

    /**
     * @return whether `this` [Tok]’s [TokKind] is `TokKind.EOF`.
     */
    fun isEof(): Boolean {
        return kind == TokKind.EOF
    }

    /**
     * @return whether `this` [Tok]’s [TokKind] is `TokKind.NEWLINE`.
     */
    fun isNewline(): Boolean {
        return kind == TokKind.NEWLINE
    }

    /**
     * @return whether `this` [Tok]’s [TokKind] is `TokKind.ERR`.
     */
    fun isErr(): Boolean {
        return kind == TokKind.ERR
    }

    /**
     * @return whether `this` [Tok]’s [TokKind] matches [other].
     */
    fun isOf(other: TokKind): Boolean {
        return kind == other
    }

    /**
     * @return The sum of the **[Locs][Loc]** of both operands, as a ([built][file.span.LocBuilder]) [Loc].
      */
    operator fun plus(other: Tok): Loc {
        return (loc + other.loc).build()
    }

    override fun toString(): String {
        return "\"$lexeme\" ($kind) $loc"
    }
}