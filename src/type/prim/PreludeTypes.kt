package type.prim

import type.gen.TypeParams
import type.rec.Rec

object PreludeTypes {
    private val INT = Rec.prelude("Int").build().wrap().into(PrimKind.INT)
    private val FLOAT = Rec.prelude("Float").build().wrap().into(PrimKind.FLOAT)
    private val BOOL = Rec.prelude("Bool").build().wrap().into(PrimKind.BOOL)

    private val STR = Rec.prelude("Str").fields(
        "len" to INT
    ).build().wrap().into(PrimKind.STR)

    private val TABLE = Rec.prelude("Table").params(
        TypeParams.from("K", "V")
    ).build().wrap().into(PrimKind.TABLE)

    private val NIL = Rec.prelude("Nil").build().wrap().into(PrimKind.NIL)

    // once possible:
    // private val NAT = Rec.prelude("Nat").build().wrap().into(PrimKind.INT).refined("self > 0".parse())
}