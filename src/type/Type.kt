package type

import type.poison.Poison
import type.prim.Prim
import type.rec.Rec
import type.refine.Refine

sealed class Type {
    data class PrimType(val prim: Prim) : Type()
    data class RecType(val rec: Rec) : Type()
    data class RefineType(val refine: Refine, val type: Type) : Type()
    data class PoisonedType(val poison: Poison) : Type()

    fun isPoisoned() = this is PoisonedType
}