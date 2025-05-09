package meta.comp.asserts

import ast.info.Info
import err.msg.Msg

/**
 * Provides two methods:
 *
 * - A [checkFor] method that is [meta assertion][MetaAssert]-specific.
 * - A [failMsg] method when the assertion fails.
 */
interface CheckAssert {
    fun checkFor(info: Info): Boolean

    fun failMsg(info: Info): Msg
}