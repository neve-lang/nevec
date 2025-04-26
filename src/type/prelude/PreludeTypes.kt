package type.prelude

import type.gen.param.TypeParams
import type.prim.PrimKind
import type.prim.into
import type.rec.Rec
import type.rec.field.Field
import type.rec.field.Tag

/**
 * Stores the Neve compiler’s prelude types as constant-like values.
 */
object PreludeTypes {
    val INT = Rec.builder().prelude("Int").build().wrap().into(PrimKind.INT)

    val FLOAT = Rec.builder().prelude("Float").build().wrap().into(PrimKind.FLOAT)
    val BOOL = Rec.builder().prelude("Bool").build().wrap().into(PrimKind.BOOL)

    val STR = Rec.builder().prelude("Str").fields(
        Field("len", INT, listOf(Tag.ALIEN))
    ).build().wrap().into(PrimKind.STR)

    val TABLE = Rec.builder().prelude("Table").params(
        TypeParams.from("K", "V")
    ).build().wrap().into(PrimKind.TABLE)

    val NIL = Rec.builder().prelude("Nil").build().wrap().into(PrimKind.NIL)

    // once possible:
    // private val NAT = Rec.builder().prelude("Nat").build().wrap().into(PrimKind.INT).refined("self > 0".parse())
}