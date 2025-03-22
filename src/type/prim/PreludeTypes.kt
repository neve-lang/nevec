package type.prim

import type.gen.param.TypeParams
import type.rec.Rec
import type.rec.field.Field
import type.rec.field.Tag

object PreludeTypes {
    private val INT = Rec.prelude("Int").build().wrap().into(PrimKind.INT)
    private val FLOAT = Rec.prelude("Float").build().wrap().into(PrimKind.FLOAT)
    private val BOOL = Rec.prelude("Bool").build().wrap().into(PrimKind.BOOL)

    private val STR = Rec.prelude("Str").fields(
        Field("len", INT, listOf(Tag.ALIEN))
    ).build().wrap().into(PrimKind.STR)

    private val TABLE = Rec.prelude("Table").params(
        TypeParams.from("K", "V")
    ).build().wrap().into(PrimKind.TABLE)

    private val NIL = Rec.prelude("Nil").build().wrap().into(PrimKind.NIL)

    // once possible:
    // private val NAT = Rec.prelude("Nat").build().wrap().into(PrimKind.INT).refined("self > 0".parse())
}