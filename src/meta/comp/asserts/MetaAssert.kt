package meta.comp.asserts

import file.span.Loc
import meta.target.AppliesTo
import meta.target.Target
import type.Type

/**
 * Represents a **meta assertion**, i.e.:
 *
 * ```
 * "Hello" @[type = Str]
 * ```
 *
 * One peculiarity of meta assertions is that they **always appear after their target**, whereas meta annotations
 * appear before it.
 */
sealed class MetaAssert : AppliesTo {
    /**
     * A **type** meta assertion.
     *
     * Used to check the [Type][type.Type] of a **primary** expression.
     *
     * @see type.Type
     */
    data class TypeAssert(val type: Type, val loc: Loc) : MetaAssert() {
        override fun appliesTo(target: Target): Boolean {
            return target == Target.PRIMARY
        }
    }
}