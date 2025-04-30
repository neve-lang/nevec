package type.hinted

import file.span.Loc
import type.NamedType
import type.Type
import type.Wrappable
import type.kind.TypeKind
import util.extension.suffixWith

/**
 * Represents a type that was **explicitly hinted by the user**.
 *
 * This distinction useful for the semantic resolver and type checker, which allows
 * the compiler to provide more accurate error messages.
 *
 * In the [pretty printer][pretty.PrettyType], these types are represented with a `..?` suffixed to them.
 *
 * When an [TypeKind.OfHinted] is confirmed to be valid, the type is extracted using [accept].
 *
 * @property type The actual type that was hinted by the user.
 * @property loc The [Loc] where the type hint was given.
 */
data class Hinted(val type: Type, val loc: Loc) : Wrappable, NamedType {
    /**
     * Accepts the hinted type as valid.
     *
     * @return the [type] that [Hinted] represents.
     */
    fun accept(): Type {
        return type
    }

    /**
     * @return whether the type being stored by the [Hinted]
     */

    override fun wrap(): TypeKind {
        return TypeKind.OfHinted(this)
    }

    override fun named(): String {
        return type.named().suffixWith("..?")
    }
}