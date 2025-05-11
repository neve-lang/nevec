package type.unresolved

import type.impl.Compare
import type.impl.NamedType
import type.impl.RecessType
import type.impl.Wrappable
import type.kind.TypeKind
import type.poison.Poison

/**
 * Represents an **unresolved type**.
 *
 * By default, all symbol references in a program are given [Unresolved], until their type becomes resolved during
 * the [semantic resolving][check.sem.SemResolver] phase.
 *
 * If the type cannot be resolved, it stays that way,
 * until the type-checking phase reports them as “unknown symbol” errors.
 */
data object Unresolved : Wrappable, NamedType, RecessType, Compare<Unresolved> {
    override fun wrap(): TypeKind {
        return TypeKind.OfUnresolved(this)
    }

    override fun named(): String {
        return "Unresolved"
    }

    override fun isSame(other: Unresolved): Boolean {
        return true
    }
}