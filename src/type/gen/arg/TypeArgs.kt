package type.gen.arg

import type.Type
import type.gen.Free
import type.impl.Compare

/**
 * Groups together a list of a **generic type argument**, i.e. types applied to types parameters of a generic type.
 *
 * @property args The type arguments themselves.
 *
 * @see type.gen.Applied
 * @see type.gen.param.TypeParams
 */
data class TypeArgs(private val args: List<Type>) : Compare<TypeArgs> {
    companion object {
        /**
         * Builds a [TypeArgs] data class from a list of [Types][Type].
         *
         * @param types The [Types][Type] in question.
         *
         * @return a [TypeArgs] data class from the given [types].
         */
        fun from(types: List<Type>): TypeArgs {
            return TypeArgs(types)
        }

        /**
         * Builds a [TypeArgs] data class from multiple [Types][Type].
         *
         * @param types The [Types][Type] in question.
         *
         * @return a [TypeArgs] data class with the given [types].
         */
        fun from(vararg types: Type): TypeArgs {
            return from(types.toList())
        }

        /**
         * Builds a [TypeArgs] data class from a list of [Free] types.
         *
         * @param frees The list of [Free] type variables.
         *
         * @return a [TypeArgs] data class with the [Free] types fully wrapped inside [Type].
         *
         * @see Free
         */
        fun frees(frees: List<Free>): TypeArgs {
            return from(frees.map(Free::covered))
        }
    }

    /**
     * @return the number of items inside [args].
     */
    fun size(): Int {
        return args.size
    }

    /**
     * @return the raw list of [args].
     */
    fun themselves(): List<Type> {
        return args
    }

    override fun isSame(other: TypeArgs): Boolean {
        return args.zip(other.themselves()).map { (a, b) -> a.isSame(b) }.all { it }
    }
}