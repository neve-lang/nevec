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
    /**
     * Checks the [MetaAssert] based on the [info] given.
     *
     * @return Whether the meta assert check did not fail.
     */
    fun checkFor(info: Info): Boolean

    /**
     * @return An error [Msg] to be associated with a [MetaAssert]’s failure.
     */
    fun failMsg(info: Info): Msg
}