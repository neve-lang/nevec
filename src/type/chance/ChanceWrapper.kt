package type.chance

import chance.Chances
import type.Type
import type.WrappedType
// import evo.Evo

data class ChanceWrapper(val chances: Chances, val type: Type, /* val evo: Evo = Evo.Id */) : WrappedType {
    companion object {
        fun none() = ChanceWrapper(Chances.OfNonPrim, Type.unknown())
    }

    // fun plug(another: Evo) = ChanceWrapper(chances, type, evo + another)

    // fun applied() = evo.applied(to = this)

    override fun wrap() = Type.ChanceType(this)

    override fun equals(other: Any?): Boolean {
        return other is ChanceWrapper && type == other.type && chances == other.chances
    }

    override fun hashCode(): Int {
        var result = chances.hashCode()
        result = 31 * result + type.hashCode()
        // result = 31 * result + evo.hashCode()
        return result
    }
}