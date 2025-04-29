package type.poison

import type.NamedType
import type.Wrappable
import type.kind.TypeKind
import util.extension.capitalize

/**
 * Represents the different kinds of type errors that can occur during the
 * type-checking phase.
 *
 * It was initially intended to represent invalid types or unknown types (i.e. invalid expressions),
 * but it was eventually extended to be more precise.
 */
enum class Poison : Wrappable, NamedType {
    /**
     * Represents an **unknown** type, i.e. a type that **could not be unified**.
     *
     * Types that depend on an [UNKNOWN] type are given [IGNORABLE].
     *
     * An example of an expression that may be given an [UNKNOWN] type could be the following:
     *
     * ```
     * ("Hello, " + 10) // TypeKind.OfPoison(UNKNOWN)
     * ```
     *
     * @see IGNORABLE
     */
    UNKNOWN,

    /**
     * Represents a poisoned type that depends on an [UNKNOWN] type to be inferred.  It is a **second degree**
     * unknown type.
     *
     * The reason why we have multiple levels of [UNKNOWN] is to control where errors get reported.
     * [UNKNOWN] types trigger a compiler error message, whereas [IGNORABLE] tells the type-checker to
     * ignore these types, as they are a direct result of another [UNKNOWN] type.
     *
     * @see UNKNOWN
     */
    IGNORABLE,

    /**
     * Represents an **unresolved type**.
     *
     * By default, all symbol references in a program are given [UNRESOLVED], until their type becomes resolved during
     * the [semantic resolving][check.sem.SemResolver] phase.
     *
     * If the type cannot be resolved, it stays that way,
     * until the type-checking phase reports them as “unknown symbol” errors.
     */
    UNRESOLVED;

    override fun wrap(): TypeKind {
        return TypeKind.OfPoison(this)
    }

    override fun named(): String {
        return toString().lowercase().capitalize()
    }
}