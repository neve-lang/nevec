package type.prelude

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
    /**
     * The `Int` prelude type.
     */
    val INT = Type(
        Rec.builder().prelude("Int").build().covered().into(PrimKind.INT),
    )

    /**
     * The `Float` prelude type.
     */
    val FLOAT = Type(
        Rec.builder().prelude("Float").build().covered().into(PrimKind.FLOAT),
    )

    /**
     * The `Bool` prelude type.
     */
    val BOOL = Type(
        Rec.builder().prelude("Bool").build().covered().into(PrimKind.BOOL),
    )

    /**
     * The `Str` prelude type.
     */
    val STR = Type(
        Rec.builder().prelude("Str").fields(
            Field("len", INT, listOf(Tag.ALIEN))
        ).build().covered().into(PrimKind.STR),
    )

    /**
     * The `Table` or `[K: V]` prelude type.
     */
    val TABLE = Type(
        Rec.builder().prelude("Table").params(
            TypeParams.from("K", "V")
        ).build().covered().into(PrimKind.TABLE),
    )

    /**
     * The `Nil` prelude type.
     */
    val NIL = Type(
        Rec.builder().prelude("Nil").build().covered().into(PrimKind.NIL),
    )

    /**
     * The immutable list of all prelude types.
     */
    val ALL = listOf(
        INT,
        FLOAT,
        BOOL,
        STR,
        TABLE,
        NIL
    )
}