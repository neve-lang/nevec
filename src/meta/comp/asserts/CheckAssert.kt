package meta.comp.asserts

import ast.info.Info
import ast.info.impl.Spanned
import err.msg.Msg
import err.pre.PreMsgs
import meta.comp.asserts.outcome.AssertOutcome

/**
 * Provides two methods:
 *
 * - A [checkFor] method that is [meta assertion][MetaAssert]-specific.
 * - A [failMsg] method when the assertion fails.
 */
interface CheckAssert : Spanned {
    /**
     * Checks the [MetaAssert] based on the [info] given.
     *
     * @return Whether the meta assert check did not fail, as an [AssertOutcome].
     *
     * @see AssertOutcome
     */
    fun checkFor(info: Info): AssertOutcome

    /**
     * @return A [Msg] based on the [AssertOutcome] of applying [checkFor] to the implementor [MetaAssert].  If it
     * is successful, `null` is returned instead, as there is nothing to report.
     */
    fun pickMsg(info: Info) = when (checkFor(info)) {
        AssertOutcome.MISSING_INPUT -> PreMsgs.missingMetaInput(loc())
        AssertOutcome.UNEXPECTED_INPUT -> PreMsgs.unexpectedMetaInput(loc())
        AssertOutcome.FAIL -> failMsg(info)
        AssertOutcome.SUCCESS -> null
    }

    /**
     * @return An error [Msg] to be associated with a [MetaAssert]’s failure.
     */
    fun failMsg(info: Info): Msg
}