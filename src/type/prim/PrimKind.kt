package type.prim

/**
 * The kind of primitive type represented by a [Prim].  The enum includes all existing primitive types in Neve.
 *
 * @see Prim
 */
enum class PrimKind {
    /**
     * The `Int` primitive type.
     */
    INT,

    /**
     * The `Float` primitive type.
     */
    FLOAT,

    /**
     * The `Bool` primitive type.
     */
    BOOL,

    /**
     * The `Str` primitive type.
     *
     * Also includes `Str8`, because their domain representations are the same.
     */
    STR,

    /**
     * The `Table K, V` or `[K: V]` primitive type.
     */
    TABLE,

    /**
     * The `Nil` primitive type.
     */
    NIL,
}