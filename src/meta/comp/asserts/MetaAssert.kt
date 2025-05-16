package meta.comp.asserts

import file.span.Loc
import meta.comp.MetaComp
import meta.target.Target
import type.Type
import ast.info.Info
import err.help.Lines
import err.msg.Msg
import err.note.Note
import err.report.Report
import meta.comp.asserts.outcome.AssertOutcome
import meta.input.Input

/**
 * Represents a **meta assertion**, i.e.:
 *
 * ```
 * "Hello" @[type == Str]
 * ```
 *
 * One peculiarity of meta assertions is that they **always appear after their target**, whereas meta annotations
 * appear before it.
 */
sealed class MetaAssert : MetaComp, CheckAssert {
    /**
     * A **type** meta assertion.
     *
     * Used to check the [Type][type.Type] of a **primary** expression.
     *
     * @see type.Type
     */
    data class TypeAssert(val type: Input<Type>, val loc: Loc) : MetaAssert() {
        override fun appliesTo(target: Target): Boolean {
            return target == Target.PRIMARY
        }

        override fun requiresInput(): Boolean {
            return true
        }

        override fun checkFor(info: Info) = when (type) {
            is Input.Absent -> AssertOutcome.MISSING_INPUT
            is Input.Present -> AssertOutcome.basedOn(
                type.itself.isSame(info.type())
            )
        }

        override fun failMsg(info: Info): Msg {
            return Report.err(loc, "meta type assertion failed").lines(
                Lines.of(
                    Note.err(info.loc(), info.type().named()),
                    Note.info(loc, "expected ${type.presence()!!.named()}")
                )
            ).build()
        }
    }

    /**
     * A **[culprit][cli.Options.CULPRITS]** meta assertion.
     *
     * **Culprits** are a `--culprits` compiler exclusive feature.  They tell the **type checker** to mark the causes
     * of [unknown types][Type.unknown] as [Culprit][type.culprit.Culprit] types.
     *
     * This [MetaAssert] checks whether the node’s type is marked as a [Culprit][type.culprit.Culprit].
     */
    data class CulpritAssert(val input: Input<Unit>, val loc: Loc) : MetaAssert() {
        override fun appliesTo(target: Target): Boolean {
            return target == Target.PRIMARY
        }

        override fun requiresInput(): Boolean {
            return false
        }

        override fun checkFor(info: Info) = when (input) {
            is Input.Present -> AssertOutcome.UNEXPECTED_INPUT
            is Input.Absent -> AssertOutcome.basedOn(
                info.type().isCulprit()
            )
        }

        override fun failMsg(info: Info): Msg {
            return Report.err(loc, "meta culprit assertion failed").lines(
                Lines.single(Note.err(info.loc(), "not a culprit"))
            ).build()
        }

    }

    override fun loc() = when (this) {
        is TypeAssert -> loc
        is CulpritAssert -> loc
    }
}