package type.kind

import type.impl.NamedType
import type.impl.Unwrappable
import type.impl.Wrappable
import type.gen.Free
import type.gen.Applied
import type.gen.Quant
import type.hinted.Hinted
import type.impl.Compare
import type.poison.Poison
import type.prim.Prim
import type.rec.Rec

/**
 * Represents all kinds of types in the Neve compiler.
 *
 * Keep in mind that this sealed class does not completely represent a type; it’s rather a wrapper union of
 * different kinds of types.
 *
 * @see type.Type
 */
sealed class TypeKind : NamedType, Compare<TypeKind> {
    /**
     * Represents a **record type**.
     *
     * In the Neve compiler, all concrete types, including primitives, are considered `OfRec`.
     *
     * The reason that the [OfRec] variant exists is to allow for **simple types**, i.e.
     * user-defined types that weren’t explicitly type-hinted (unlike [OfHinted]) and
     * haven’t been given any generic types.
     *
     * An example of a simple type could be:
     *
     * ```
     * rec Person
     *   name: Str
     *   age: Whole
     * end
     * ```
     *
     * [Prim] types are not considered simple types due to their associated [domain representation][domain.Domain].
     *
     * @param rec The [Rec] being wrapped by the variant.
     *
     * @see OfHinted
     * @see Rec
     */
    data class OfRec(val rec: Rec) : Unwrappable<Rec>, TypeKind() {
        override fun itself(): Rec {
            return rec
        }
    }

    /**
     * Wrapper around a **primitive type**.
     *
     * @param prim The [Prim] being wrapped by the variant.
     *
     * @see Prim
     */
    data class OfPrim(val prim: Prim) : Unwrappable<Prim>, TypeKind() {
        override fun itself(): Prim {
            return prim
        }
    }

    /**
     * Represents a type that was **explicitly hinted by the user**.
     *
     * @param hinted The [Hinted] being wrapped by the variant.
     *
     * @see Hinted
     */
    data class OfHinted(val hinted: Hinted) : Unwrappable<Hinted>, TypeKind() {
        override fun itself(): Hinted {
            return hinted
        }
    }

    /**
     * Wrapper around a type with generic type arguments applied.
     *
     * @param applied The [Applied] being wrapped by the variant.
     *
     * @see Applied
     */
    data class OfApplied(val applied: Applied) : Unwrappable<Applied>, TypeKind() {
        override fun itself(): Applied {
            return applied
        }
    }

    /**
     * Wrapper around a **free type**.
     *
     * @param free The [Free] being wrapped by the variant.
     *
     * @see Free
     */
    data class OfFree(val free: Free) : Unwrappable<Free>, TypeKind() {
        override fun itself(): Free {
            return free
        }
    }

    /**
     * Wrapper around a **quantified** or **generalized type**.
     *
     * @param quant The [Quant] being wrapped by the variant.
     *
     * @see Quant
     */
    data class OfQuant(val quant: Quant) : Unwrappable<Quant>, TypeKind() {
        override fun itself(): Quant {
            return quant
        }
    }

    /**
     * Wrapper around a **poison type**.
     *
     * @param poison The [Poison] being wrapped by the variant.
     *
     * @see Poison
     */
    data class OfPoison(val poison: Poison) : Unwrappable<Poison>, TypeKind() {
        override fun itself(): Poison {
            return poison
        }
    }

    companion object {
        /**
         * @return A poisoned type [OfPoison] with [Poison.Unresolved].
         */
        fun unresolved(): OfPoison {
            return OfPoison(Poison.Unresolved)
        }

        /**
         * @param name The name of the type, as it appears in the code.
         *
         * @return A poisoned type [OfPoison] with [Poison.Undefined].
         */
        fun undefined(name: String): OfPoison {
            return OfPoison(Poison.Undefined(name))
        }

        /**
         * @return A poisoned type [OfPoison] with [Poison.Unknown].
         */
        fun unknown(): OfPoison {
            return OfPoison(Poison.Unknown)
        }
    }

    /**
     * Unwraps the **original type** wrapped inside the [TypeKind] wrapper as a [Wrappable].
     *
     * @return the original type.
     *
     * @see Wrappable
     */
    fun unwrapped(): Wrappable = when (this) {
        is OfRec -> itself()
        is OfPrim -> itself()
        is OfHinted -> itself()
        is OfApplied -> itself()
        is OfPoison -> itself()
        is OfFree -> itself()
        is OfQuant -> itself()
    }

    override fun named(): String = when (this) {
        is OfRec -> rec.named()
        is OfPrim -> prim.named()
        is OfHinted -> hinted.named()
        is OfApplied -> applied.named()
        is OfPoison -> poison.named()
        is OfFree -> free.named()
        is OfQuant -> quant.named()
    }

    override fun isSame(other: TypeKind): Boolean {
        if (this::class != other::class) {
            return false
        }

        return when (this) {
            is OfRec -> rec.isSame(other.unwrapped() as Rec)
            is OfPrim -> prim.isSame(other.unwrapped() as Prim)
            is OfHinted -> hinted.isSame(other.unwrapped() as Hinted)
            is OfApplied -> applied.isSame(other.unwrapped() as Applied)
            is OfPoison -> poison.isSame(other.unwrapped() as Poison)
            is OfFree -> free.isSame(other.unwrapped() as Free)
            is OfQuant -> quant.isSame(other.unwrapped() as Quant)
        }
    }
}