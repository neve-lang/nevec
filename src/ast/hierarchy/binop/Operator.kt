package ast.hierarchy.binop

/**
 * Denotes all kinds of possible operators for a BinOp.
 */
enum class Operator {
    PLUS,
    MINUS,
    MUL,
    DIV,

    SHL,
    SHR,
    BIT_AND,
    BIT_OR,
    BIT_XOR,

    NEQ,
    EQ,
    GT,
    GTE,
    LT,
    LTE,
}