package type.poison

import file.span.Loc
import type.NamedType
import type.Type
import type.Wrappable
import type.kind.TypeKind
import util.extension.capitalize
import util.extension.suffixWith

/**
 * Represents the different kinds of type errors that can occur during the
 * type-checking phase.
 *
 * It was initially intended to represent invalid types or unknown types (i.e. invalid expressions),
 * but it was eventually extended to be more precise.
 */
sealed class Poison : Wrappable, NamedType {
    /**
     * Represents an **unknown** type, i.e. a type that **could not be unified**.
     *
     * Types that depend on an [Unknown] type are given [Ignorable].
     *
     * An example of an expression that may be given an [Unknown] type could be the following:
     *
     * ```
     * ("Hello, " + 10) // TypeKind.OfPoison(Unknown)
     * ```
     *
     * @see Ignorable
     */
    data object Unknown : Poison()

    /**
     * Represents a poisoned type that depends on an [Unknown] type to be inferred.  It is a **second degree**
     * unknown type.
     *
     * The reason why we have multiple levels of [Unknown] is to control where errors get reported.
     * [Unknown] types trigger a compiler error message, whereas [Ignorable] tells the type-checker to
     * ignore these types, as they are a direct result of another [Unknown] type.
     *
     * @see Unknown
     */
    data object Ignorable : Poison()

    /**
     * Represents an **unresolved type**.
     *
     * By default, all symbol references in a program are given [Unresolved], until their type becomes resolved during
     * the [semantic resolving][check.sem.SemResolver] phase.
     *
     * If the type cannot be resolved, it stays that way,
     * until the type-checking phase reports them as “unknown symbol” errors.
     */
    data object Unresolved : Poison()

    /**
     * Represents a hinted type that does not conform with the inferred type.
     *
     * During the type inference phase, a [Hinted][type.hinted.Hinted] type may be unified with the actual inferred
     * type.  If the unification is unsuccessful, a poisoned type will be given to the new [Hinted][type.hinted.Hinted]
     * type.
     *
     * In the pretty printer, these types are represented with a `..!` following their name.
     *
     * @param loc The [Loc] where the type was originally hinted.
     *
     * @see type.hinted.Hinted
     */
    data class Hint(val original: Type, val loc: Loc) : Poison()

    override fun wrap(): TypeKind {
        return TypeKind.OfPoison(this)
    }

    override fun named() = when (this) {
        is Unknown -> "Unknown"
        is Ignorable -> "Ignorable"
        is Unresolved -> "Unresolved"
        is Hint -> original.named().suffixWith("..!")
    }
}