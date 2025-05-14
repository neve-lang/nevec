package ast.hierarchy.binop.operator

import tok.TokKind

/**
 * Valid bitwise operators in Neve.
 */
enum class BitwiseOperator {
    SHL, SHR, BIT_AND, BIT_XOR, BIT_OR;

    companion object {
        /**
         * @return a [BitwiseOperator] from a [TokKind].
         *
         * @throws IndexOutOfBoundsException if [canBeBitwise] is false for [kind].
         */
        fun from(kind: TokKind): BitwiseOperator {
            return BitwiseOperator.entries[kind.clampedFrom(TokKind.SHL)]
        }
    }
}

/**
 * @return whether [this] can be mapped to [ArithOperator.entries] if `clampedFrom(TokKind.SHL)`.
 */
fun TokKind.canBeBitwise(): Boolean {
    return isInBetween(TokKind.SHL, TokKind.BIT_OR)
}
