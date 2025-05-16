package type.culprit

import type.Type
import type.impl.Compare
import type.impl.NamedType
import type.impl.Wrappable
import type.kind.TypeKind

/**
 * Simple wrapper type that denotes a **[culprit][cli.Options.CULPRITS]** type.  It is exclusive to the
 * `--culprits` feature, and does not appear in any stage of compilation before type-checking.
 *
 * Culprit types are represented with two exclamation marks before them, such as: `!!Int`.
 *
 * Unification of culprit types is undefined, and an exception will be thrown in such a case.
 *
 * @see cli.Options.CULPRITS
 */
data class Culprit(val type: Type) : NamedType, Wrappable, Compare<Culprit> {
    override fun named(): String {
        return "!!${type.named()}"
    }

    override fun wrap(): TypeKind {
        return TypeKind.OfCulprit(this)
    }

    override fun isSame(other: Culprit): Boolean {
        return type.isSame(other.type)
    }
}