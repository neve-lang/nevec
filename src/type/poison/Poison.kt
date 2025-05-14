package type.poison

import file.span.Loc
import type.impl.NamedType
import type.Type
import type.impl.Compare
import type.impl.RecessType
import type.impl.Wrappable
import type.kind.TypeKind
import util.extension.suffixWith

/**
 * Represents the different kinds of type errors that can occur during the
 * type-checking phase.
 *
 * It was initially intended to represent invalid types or unknown types (i.e. invalid expressions),
 * but it was eventually extended to be more precise.
 *
 * Poisoned types are defined as [RecessTypes][RecessType] due to their special unification behavior.
 */
sealed class Poison : Wrappable, NamedType, RecessType, Compare<Poison> {
    companion object {
        private val NAMES = mapOf(
            "Unknown" to Unknown,
            "Ignorable" to Ignorable,
        )

        /**
         * @return A [Poison] type from a string name, if it is one of the valid candidates.  Otherwise, it returns
         * `null`.
         *
         * Valid candidates include:
         *
         * - `"Unknown"`
         * - `"Ignorable"`
         * - `"Unresolved"`
         */
        fun fromName(name: String): Poison? {
            return NAMES[name]
        }
    }

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
     * The only exception to this rule are [Parens][ast.hierarchy.expr.Expr.Parens] nodes, as it allows the full
     * parenthesized expression—including the parentheses—to be highlighted in type errors.
     *
     * This behavior is monitored by the [SemResolver][check.sem.SemResolver] at its `visitParens` method, and special
     * error reporting is implemented by the type checker when it comes to parenthesized expressions.
     *
     * @see Unknown
     */
    data object Ignorable : Poison()

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

    /**
     * Represents an undefined type, that is, a type that was not registered into the symbol table prior to
     * its first usage.
     *
     * @param name The name of the undefined type, as it was used in the code.
     */
    data class Undefined(val name: String) : Poison()

    override fun wrap(): TypeKind {
        return TypeKind.OfPoison(this)
    }

    override fun named() = when (this) {
        is Unknown -> "Unknown"
        is Ignorable -> "Ignorable"
        is Undefined -> "Undefined ‘${name}’"
        is Hint -> original.named().suffixWith("..!")
    }

    override fun isSame(other: Poison): Boolean {
        return named() == other.named()
    }

    override fun toString(): String {
        return "~${named()}"
    }
}