package ast.hierarchy.binop

import lex.isInBetween
import tok.Tok
import tok.TokKind

/**
 * Denotes all kinds of possible operators for a BinOp.
 */
enum class Operator {
    PLUS, MINUS, MUL, DIV,

    SHL, SHR, BIT_AND, BIT_XOR, BIT_OR,

    NEQ, EQ, GT, GTE, LT, LTE;

    companion object {
        fun from(tok: Tok) = from(tok.kind)

        // it's slightly disappointing that we have to resort to optional types in Kotlin, but hey, beggars
        // can't be choosers.
        // but another awesome thing about Neve is that you wouldn't have to!  here's an example:
        //
        // ```
        // fun from(kind OpKind)
        // with OpKind = TokKind where TokKind.Plus <= self <= TokKind.Lte
        // ```
        //
        // then, we wouldn't could *always* return a valid operator!
        fun from(kind: TokKind): Operator? {
            if (!kind.isOpKind()) {
                return null
            }

            return entries[kind.clamped()]
        }
    }
}

/**
 * Checks whether [this] can be mapped to [Operator.entries] if [clamped].
 */
fun TokKind.isOpKind() = isInBetween(TokKind.PLUS, TokKind.LTE)

/**
 * Returns an *ordinal* that can be mapped to [Operator.entries] **if and only if** the TokKind in question [isOpKind].
 */
fun TokKind.clamped() = ordinal - TokKind.PLUS.ordinal