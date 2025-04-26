package ast.hierarchy.binop.operator

import tok.Tok
import tok.TokKind

/**
 * Sealed class around multiple kinds of operators.
 *
 * Multiple enum classes are defined for exhaustiveness.
 *
 * In Neve, we would simply use a refinement type.
 */
sealed class Operator {
    /**
     * Wrapper around [BitwiseOperator].
     *
     * @see BitwiseOperator
     */
    data class OfBitwise(val operator: BitwiseOperator) : Operator() {
        /**
         * @return the [operator] itself.
         */
        fun itself(): BitwiseOperator {
            return operator
        }
    }

    /**
     * Wrapper around [ArithOperator].
     *
     * @see ArithOperator
     */
    data class OfArith(val operator: ArithOperator) : Operator() {
        /**
         * @return the [operator] itself.
         */
        fun itself(): ArithOperator {
            return operator
        }
    }

    /**
     * Wrapper around [CompOperator].
     *
     * @see CompOperator
     */
    data class OfComp(val operator: CompOperator) : Operator() {
        /**
         * @return the [operator] itself.
         */
        fun itself(): CompOperator {
            return operator
        }
    }

    /**
     * Wrapper around [ConcatOperator].
     *
     * @see ConcatOperator
     */
    data class OfConcat(val operator: ConcatOperator) : Operator() {
        /**
         * @return the [operator] itself.
         */
        fun itself(): ConcatOperator {
            return operator
        }
    }

    companion object {
        /**
         * Tries to return an [Operator] based on a [Tok].
         *
         * Does not include [ConcatOperator], as the [OfConcat] node only exists post semantic
         * resolving.
         *
         * @param tok The [Tok] in question.
         *
         * @return An [Operator] if [tok]’s kind [isOpKind], `null` otherwise.
         */
        fun from(tok: Tok): Operator? {
            return from(tok.kind)
        }

        /**
         * Tries to return an [Operator] based on a [TokKind].
         *
         * Does not include [ConcatOperator], as the [OfConcat] node only exists post semantic
         * resolving.
         *
         * @param kind The [TokKind] in question.
         *
         * @return An [Operator] if [kind] can be mapped to an operator, `null` otherwise.
         */
        fun from(kind: TokKind): Operator? {
            return when {
                // it's slightly disappointing that we have to resort to optional types in Kotlin, but hey, beggars
                // can't be choosers.
                // but another awesome thing about Neve is that you wouldn't have to!  here's an example:
                //
                // ```
                // fun from(kind OpKind)
                // with OpKind = TokKind where TokKind.Plus <= self <= TokKind.Lte
                // ```
                //
                // then, we could *always* return a valid operator!
                kind.canBeArith() -> OfArith(ArithOperator.from(kind))
                kind.canBeBitwise() -> OfBitwise(BitwiseOperator.from(kind))
                kind.canBeComp() -> OfComp(CompOperator.from(kind))
               else -> null
            }
        }
    }
}