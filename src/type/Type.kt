package type

import type.chance.ChanceWrapper
import type.gen.Free
import type.gen.Gen
import type.hinted.Hinted
import type.poison.Poison
import type.prim.Prim
import type.rec.Rec
import type.refine.Refine

/**
 * Represents all kinds of types in the Neve compiler.
 */
sealed class Type {
    data class PrimType(val prim: Prim) : Type()
    data class RecType(val rec: Rec) : Type()
    data class HintedType(val hinted: Hinted) : Type()
    data class GenType(val gen: Gen) : Type()
    data class FreeType(val free: Free) : Type()

    data class RefineType(val refine: Refine, val type: Type) : Type()

    data class ChanceType(val chance: ChanceWrapper) : Type()

    data class PoisonedType(val poison: Poison) : Type()

    companion object {
        fun unresolved() = PoisonedType(Poison.UNRESOLVED)

        fun unknown() = PoisonedType(Poison.UNKNOWN)
    }

    fun isPoisoned() = this is PoisonedType

    fun isFree() = this is FreeType

    fun name(): String? = when (this) {
        is RecType -> rec.name
        is PrimType -> prim.type.name()
        is RefineType -> type.name()
        is HintedType -> hinted.type.name()
        is GenType -> gen.type.name()
        is ChanceType -> chance.type.name()

        is FreeType -> null
        is PoisonedType -> null
    }

    fun moduleName(): String? = when (this) {
        is RecType -> rec.module.name
        is PrimType -> prim.type.moduleName()
        is RefineType -> type.moduleName()
        is HintedType -> hinted.type.moduleName()
        is GenType -> gen.type.moduleName()
        is ChanceType ->  chance.type.moduleName()

        is FreeType -> null
        is PoisonedType -> null
    }

    fun fullName() = moduleName() + "." + name()

    override fun equals(other: Any?) = when {
        this is ChanceType && other is ChanceType -> chance == other.chance
        else -> other is Type && fullName() == other.fullName()
    }

    override fun hashCode() = javaClass.hashCode()
}