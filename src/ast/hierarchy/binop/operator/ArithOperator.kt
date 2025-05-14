package ast.hierarchy.binop.operator

import tok.TokKind

/**
 * Valid (and currently supported) arithmetic operators in Neve.
 *
 * Other operators, such as modulo or exponentiation, will be implemented later.
 */
enum class ArithOperator {
    ADD, SUB, MUL, DIV;

    companion object {
        /**
         * @return an [ArithOperator] from a [TokKind].
         *
         * @throws IndexOutOfBoundsException if [canBeArith] is false for [kind].
         */
        fun from(kind: TokKind): ArithOperator {
            return entries[kind.clampedFrom(TokKind.PLUS)]
        }
    }
}

/**
 * @return whether [this] can be mapped to [ArithOperator.entries] if `clampedFrom(TokKind.PLUS)`.
 */
fun TokKind.canBeArith(): Boolean {
    return isInBetween(TokKind.PLUS, TokKind.SLASH)
}
