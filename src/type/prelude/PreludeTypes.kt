package type.prelude

import domain.Domain
import type.Type
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
    val INT = Type(
        Rec.builder().prelude("Int").build().wrap().into(PrimKind.INT),
        Domain.Undefined
    )

    val FLOAT = Type(
        Rec.builder().prelude("Float").build().wrap().into(PrimKind.FLOAT),
        Domain.Undefined
    )
    val BOOL = Type(
        Rec.builder().prelude("Bool").build().wrap().into(PrimKind.BOOL),
        Domain.Undefined
    )

    val STR = Type(
        Rec.builder().prelude("Str").fields(
            Field("len", INT, listOf(Tag.ALIEN))
        ).build().wrap().into(PrimKind.STR),
        Domain.Undefined
    )

    val TABLE = Type(
        Rec.builder().prelude("Table").params(
            TypeParams.from("K", "V")
        ).build().wrap().into(PrimKind.TABLE),
        Domain.Undefined
    )

    val NIL = Type(
        Rec.builder().prelude("Nil").build().wrap().into(PrimKind.NIL),
        Domain.Undefined
    )
}