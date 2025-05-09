package tok

/**
 * A kind of token.
 *
 * Each keyword is treated as a distinct token.
 */
enum class TokKind {
    SEMICOL, COL, COMMA, DOT, DOT_DOT,

    PLUS, MINUS, STAR, SLASH,

    SHL, SHR, BIT_AND, BIT_XOR, BIT_OR,

    NEQ, EQ, GT, GTE, LT, LTE,

    ASSIGN,

    EXCLAM, QUESTION,

    AND, CONST, DO, ELSE, END, FOR, FUN, IF, IN, IS, LET, MATCH, OR, PRINT, RETURN, UNION, VAR, WHILE, NOT_IN, IS_NOT,

    RPAREN, RBRACKET, PIPE,

    LPAREN, LBRACKET,

    ID, STR, INT, FLOAT, INTERPOL,

    FALSE, NIL, NOT, SELF, TRUE, WITH,

    INTERPOL_SEP,

    META_ASSERT,

    APOSTROPHE, TILDE,

    NEWLINE, ERR, EOF;

    /**
     * @return whether `this` kind could be a token that starts a statement.
     */
    fun isStmtStarter(): Boolean {
        return isInBetween(ELSE, WHILE)
    }

    /**
     * @return whether `this` kind could be a token that starts an expression.
     */
    fun isExprStarter(): Boolean {
        return isInBetween(LPAREN, WITH)
    }

    /**
     * @return whether `this` kind could be a token that starts a type.  It includes:
     *
     * - `ID`, for simple identifiers
     * - `LBRACKET`, for lists and tables
     * - `APOSTROPHE`, for [free types][type.gen.Free]
     */
    fun isTypeStarter(): Boolean {
        return this == ID || this == LBRACKET || this == APOSTROPHE
    }

    /**
     * @return whether [ordinal] is between the ordinals of [min] and [max], **inclusive**.
     */
    fun isInBetween(min: TokKind, max: TokKind): Boolean {
        return ordinal >= min.ordinal && ordinal <= max.ordinal
    }

    /**
     * Often used when converting [TokKinds][TokKind] to other kinds of enums whose entries can be
     * mapped to [TokKind], when the [TokKind] is clamped (i.e.
     * [ArithOperator][ast.hierarchy.binop.operator.ArithOperator]).
     *
     * @param kind The [TokKind] whose [ordinal] should be used to clamp.
     *
     * @return `this` [ordinal] minus the ordinal of [kind].
     */
    fun clampedFrom(kind: TokKind): Int {
        return ordinal - kind.ordinal
    }
}