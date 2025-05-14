package type.gen

import type.impl.NamedType
import type.Type
import type.kind.TypeKind
import type.impl.Wrappable
import type.gen.param.TypeParams
import type.gen.arg.TypeArgs
import type.impl.Compare

/**
 * Represents a type with a list of generic type arguments applied to it.
 *
 * @property args The type parameters in question.
 * @property type The type to which the [args] need to be applied to.
 *
 * @see TypeParams
 */
data class Applied(val args: TypeArgs, val type: Type) : Wrappable, NamedType, Compare<Applied> {
    /**
     * @return the number of items inside [args].
     */
    fun argCount(): Int {
        return args.size()
    }

    /**
     * @return the raw list of [args], without the [TypeArgs] wrapper.
     */
    fun argsList(): List<Type> {
        return args.themselves()
    }

    override fun wrap(): TypeKind {
        return TypeKind.OfApplied(this)
    }

    override fun named(): String {
        return "${type.named()} (${argsList().joinToString(",\n", transform = Type::named)})"
    }

    override fun isSame(other: Applied): Boolean {
        return type.isSame(other.type) && args.isSame(other.args)
    }

    override fun toString(): String {
        return named()
    }
}