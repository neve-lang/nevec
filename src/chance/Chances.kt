package chance

import chance.repr.bool.BoolChances
import chance.repr.num.NumChances
import chance.repr.str.StrChances

/**
 * Chances represent a type's *possible values*.  Their possible value representation depends on the type being
 * described.
 *
 * Primitive types ([type.prim.Prim]) are given a special internal chance representation, whereas user-defined
 * records are given the [OfNonPrim] representation; the fields of a record type are given different representations,
 * however.
 */
sealed class Chances {
    data class OfNum(val chances: NumChances) : Chances()
    data class OfStr(val chances: StrChances) : Chances()
    data class OfBool(val chances: BoolChances) : Chances()
    // TODO: add chance representation for tables

    data object OfNil : Chances()
    data object OfNonPrim : Chances()
}