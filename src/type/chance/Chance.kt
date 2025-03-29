package type.chance

import type.Type
import type.WrappedType
import evo.Evo

data class Chance(val chances: Chances, val type: Type, val evo: Evo = Evo.id()) : WrappedType {
    fun plug(another: Evo) = Chance(chances, type, evo + another)

    fun applied() = evo.applied(to = this)

    override fun wrap() = Type.ChanceType(this)
}

fun Chances?.intoType(type: Type) = if (this == null)
    Chance(Chances.OfNonPrim, Type.unknown())
else
    Chance(this, type)