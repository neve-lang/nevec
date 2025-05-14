package type.gen

import type.impl.Compare
import type.impl.NamedType
import type.impl.RecessType
import type.kind.TypeKind
import type.impl.Wrappable
import util.extension.prefixWith

/**
 * Describes a **free type variable**, i.e. a type variable that wasn’t generalized yet in Hindley-Milner parlance.
 *
 *
 * @property id The free type variable’s id.
 * @property level The free type variable’s level.
 */
data class Free(val id: Int, val level: Int) : Wrappable, NamedType, RecessType, Compare<Free> {
    override fun wrap(): TypeKind {
        return TypeKind.OfFree(this)
    }

    override fun named(): String {
        return id.toString().prefixWith("'")
    }

    override fun isSame(other: Free): Boolean {
        return id == other.id && level == other.level
    }

    override fun toString(): String {
        return named()
    }
}