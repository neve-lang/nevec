package ast.hierarchy.binop.operator

import tok.TokKind

/**
 * Valid comparison operators in Neve.
 */
enum class CompOperator {
    NEQ, EQ, GT, GTE, LT, LTE;

    companion object {
        /**
         * @return a [CompOperator] from a [TokKind].
         *
         * @throws IndexOutOfBoundsException if [canBeComp] is false for [kind].
         */
        fun from(kind: TokKind): CompOperator {
            return CompOperator.entries[kind.clampedFrom(TokKind.NEQ)]
        }
    }
}

/**
 * @return whether [this] can be mapped to [ArithOperator.entries] if `clampedFrom(TokKind.NEQ)`.
 */
fun TokKind.canBeComp(): Boolean {
    return isInBetween(TokKind.NEQ, TokKind.LTE)
}
