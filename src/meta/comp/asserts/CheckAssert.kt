package meta.comp.asserts

import ast.info.Info

/**
 * Provides a [checkFor] method that is [meta assertion][MetaAssert]-specific.
 */
interface CheckAssert {
    fun checkFor(info: Info): Boolean
}