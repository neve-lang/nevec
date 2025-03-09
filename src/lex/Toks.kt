package lex

import tok.TokKind
import tok.TokKind.*

/**
 * Abstracts away the process of matching [tok.Tok]s based on their lexemes.
 */
object Toks {
    const val MAX_TOK_LEN = 3

    private val KEYWORDS = mapOf(
        "and" to AND,
        "const" to CONST,
        "do" to DO,
        "else" to ELSE,
        "end" to END,
        "for" to FOR,
        "fun" to FUN,
        "if" to IF,
        "in" to IN,
        "is" to IS,
        "let" to LET,
        "match" to MATCH,
        "or" to OR,
        "print" to PRINT,
        "return" to RETURN,
        "union" to UNION,
        "var" to VAR,
        "while" to WHILE,
        "false" to FALSE,
        "nil" to NIL,
        "not" to NOT,
        "self" to SELF,
        "true" to TRUE,
        "with" to WITH,
        "band" to BIT_AND,
        "xor" to BIT_XOR,
        "bor" to BIT_OR,
    )

    private val ONE_CHAR_TOKS = mapOf(
        ';' to SEMICOL,
        ':' to COL,
        ',' to COMMA,
        '.' to DOT,
        '+' to PLUS,
        '-' to MINUS,
        '*' to STAR,
        '/' to SLASH,
        '>' to GT,
        '<' to LT,
        '=' to ASSIGN,
        '!' to EXCLAM,
        '?' to QUESTION,
        ')' to RPAREN,
        ']' to RBRACKET,
        '|' to PIPE,
        '(' to LPAREN,
        '[' to LBRACKET,
    )

    private val TWO_CHAR_TOKS = mapOf(
        ".." to DOT_DOT,
        "<<" to SHL,
        ">>" to SHR,
        "+=" to PLUS_ASSIGN,
        "-=" to MINUS_ASSIGN,
        "*=" to STAR_ASSIGN,
        "/=" to SLASH_ASSIGN,
        "!=" to NEQ,
        "==" to EQ,
        ">=" to GTE,
        "<=" to LTE,
    )

    private val THREE_CHAR_TOKS = mapOf(
        "<<=" to SHL_ASSIGN,
        ">>=" to SHR_ASSIGN,
    )

    // something super cool about Neve is that it wouldn't require you to have an 'else' clause in this case!!
    // here's how that would look like:
    //
    // fun find(lexeme TokLexeme)
    // with TokLexeme = Str where 1 <= self.len <= 3
    //   match lexeme.len
    //     | 3 = Self.ThreeCharToks[lexeme]
    //     | 2 = Self.TwoCharToks[lexeme]
    //     | 1 = Self.OneCharToks[lexeme.char]
    //   end
    // end
    fun findTok(lexeme: String) = when (lexeme.length) {
        3 -> THREE_CHAR_TOKS[lexeme]
        2 -> TWO_CHAR_TOKS[lexeme]
        1 -> ONE_CHAR_TOKS[lexeme.first()]
        else -> null
    }

    fun findKeyword(lexeme: String) = KEYWORDS[lexeme]

    fun lexemeOf(kind: TokKind): String? {
        return ONE_CHAR_TOKS.key(from = kind)?.toString() ?: TWO_CHAR_TOKS.key(from = kind)
        ?: THREE_CHAR_TOKS.key(from = kind)
    }
}

fun <K, V> Map<K, V>.key(from: V): K? = filterValues { it == from }.keys.firstOrNull()

fun TokKind.lexeme() = Toks.lexemeOf(this)

fun TokKind.isStmtStarter() = inBetween(ELSE, WHILE)

fun TokKind.inBetween(min: TokKind, max: TokKind) = ordinal >= min.ordinal && ordinal <= max.ordinal
