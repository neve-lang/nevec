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

    PLUS_ASSIGN, MINUS_ASSIGN, STAR_ASSIGN, SLASH_ASSIGN, SHL_ASSIGN, SHR_ASSIGN,

    NEQ, EQ, GT, GTE, LT, LTE,

    ASSIGN,

    EXCLAM, QUESTION,

    AND, DO, ELSE, END, FOR, FUN, IF, IN, IS, LET, MATCH, OR, PRINT, RETURN, UNION, VAR, WHILE,
    NOT_IN, IS_NOT,

    RPAREN, RBRACKET, PIPE,

    LPAREN, LBRACKET,

    ID, STR, INT, FLOAT, INTERPOL,

    FALSE, NIL, NOT, SELF, TRUE, WITH,

    INTERPOL_SEP,

    NEWLINE, ERR, EOF;
}