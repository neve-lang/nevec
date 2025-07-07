package opt.transform

import ir.data.change.Change
import ir.structure.impl.IrStructure
import ir.term.warm.Warm

/**
 * A Transform represents a pure modification that is applied to an [IrFun][ir.structure.fun.IrFun] during an
 * optimization pass.
 *
 * It allows us to implement optimization passes in a declarative way, while still maintaining flexibility on the
 * kind of [IR structure][IrStructure] we’re optimizing for.
 *
 * Transforms typically also carry a list of [Changes][Change] that are provided by the optimization pass itself.
 * For example, a constant folding pass may emit a list of [Unuse][ir.data.change.TermChange.Unuse] changes
 * for the IR terms that were removed.  This would allow later passes, such as Dead Term Elimination, to
 * clean up the IR.
 *
 * @param S The kind of IR structure the transform applies to.
 */
sealed class Transform<S : IrStructure> {
    /**
     * Represents an identity transform: no modifications are made.
     *
     * For this reason, it does not carry a list of [Changes][Change].
     *
     * @property retained The IR structure to be retained.
     */
    data class Retain<S : IrStructure>(val retained: S) : Transform<S>()

    /**
     * Describes a replacement that is made to the original [IR structure][IrStructure].
     *
     * When an optimization pass modifies something, it always uses the [Replace] Transform.
     */
    data class Replace<S : IrStructure>(val new: S, val changes: List<Change<Warm>>) : Transform<S>()

    /**
     * @return A list of [Change] that are associated with `this` [Transform].  The Changes are always [Warm].
     *
     * If a Transform does not have any changes associated with it—such as the [Retain] transform, an empty list
     * is returned instead.
     */
    fun changes() = when (this) {
        is Retain -> emptyList()
        is Replace -> changes
    }
}