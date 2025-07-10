package opt.help

import ir.structure.op.Op
import ir.structure.tac.Tac
import ir.term.warm.Warm

/**
 * Helper object that simplifies the process of replacing certain [Terms][ir.term.TermLike] in an
 * [IR operation][Op] with a set of others.
 *
 * [TermReplacer] only applies to [warm IR][ir.term.TermLike].
 */
object TermReplacer {
    /**
     * @return A copy of the given [Op] with the [toBeReplaced] terms replaced with [newTerms].
     *
     * [TermReplacer] assumes there is a one-to-one correspondence between both lists.  For example, if given the lists:
     *
     * ```
     * [1, 2, 3]
     * [9, 8, 7]
     * ```
     *
     * `1` will be replaced with `9`, `2` with `8`, and `3` with `7`.
     *
     * If the size of [toBeReplaced] does not match [newTerms], [IllegalArgumentException] is thrown.
     *
     * If the [Op] does not contain some terms in [toBeReplaced], no replacement is done for those terms.
     *
     * If both lists are empty, no replacement is done.
     */
    fun replace(
        op: Op<Warm>,
        toBeReplaced: List<Warm>,
        newTerms: List<Warm>
    ): Op<Warm> {
        require(toBeReplaced.size == newTerms.size) {
            "`toBeReplaced` and `newTerms` must have the same size."
        }

        return when (op) {
            is Op.Const -> replaceConst(op, toBeReplaced, newTerms)
            is Op.Ret -> replaceRet(op, toBeReplaced, newTerms)
            is Op.Print -> replacePrint(op, toBeReplaced, newTerms)
            is Op.OfTac -> replaceTac(op.tac, toBeReplaced, newTerms)
            is Op.Dummy -> op
        }
    }

    private fun replaceConst(op: Op.Const<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.Const<Warm> {
        val map = replacementMap(op, replacand, newTerms)

        return op.copy(
            to = getOrSame(map, op.term())
        )
    }

    private fun replaceRet(op: Op.Ret<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.Ret<Warm> {
        val map = replacementMap(op, replacand, newTerms)

        return op.copy(
            term = getOrSame(map, op.term)
        )
    }

    private fun replacePrint(op: Op.Print<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.Print<Warm> {
        val map = replacementMap(op, replacand, newTerms)

        return op.copy(
            term = getOrSame(map, op.term)
        )
    }

    private fun replaceTac(tac: Tac<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        return when (tac) {
            is Tac.TableSet -> replaceTableSet(tac, replacand, newTerms)
            is Tac.Eq -> replaceEq(tac, replacand, newTerms)
            is Tac.Neq -> replaceNeq(tac, replacand, newTerms)
            is Tac.Gte -> replaceGte(tac, replacand, newTerms)
            is Tac.Gt -> replaceGt(tac, replacand, newTerms)
            is Tac.Lte -> replaceLte(tac, replacand, newTerms)
            is Tac.Lt -> replaceLt(tac, replacand, newTerms)
            is Tac.Shr -> replaceShr(tac, replacand, newTerms)
            is Tac.Shl -> replaceShl(tac, replacand, newTerms)
            is Tac.BitOr -> replaceBitOr(tac, replacand, newTerms)
            is Tac.BitAnd -> replaceBitAnd(tac, replacand, newTerms)
            is Tac.BitXor -> replaceBitXor(tac, replacand, newTerms)
            is Tac.Add -> replaceAdd(tac, replacand, newTerms)
            is Tac.Sub -> replaceSub(tac, replacand, newTerms)
            is Tac.Mul -> replaceMul(tac, replacand, newTerms)
            is Tac.Div -> replaceDiv(tac, replacand, newTerms)
            is Tac.Concat -> replaceConcat(tac, replacand, newTerms)
            is Tac.Neg -> replaceNeg(tac, replacand, newTerms)
            is Tac.Not -> replaceNot(tac, replacand, newTerms)
            is Tac.Show -> replaceShow(tac, replacand, newTerms)
        }
    }

    private fun replaceTableSet(tac: Tac.TableSet<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            key = getOrSame(map, tac.key),
            value = getOrSame(map, tac.value)
        ).wrap()
    }

    private fun replaceEq(tac: Tac.Eq<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceNeq(tac: Tac.Neq<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceGte(tac: Tac.Gte<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceGt(tac: Tac.Gt<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceLte(tac: Tac.Lte<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceLt(tac: Tac.Lt<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceShl(tac: Tac.Shl<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceShr(tac: Tac.Shr<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceBitOr(tac: Tac.BitOr<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceBitAnd(tac: Tac.BitAnd<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceBitXor(tac: Tac.BitXor<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceAdd(tac: Tac.Add<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceSub(tac: Tac.Sub<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceMul(tac: Tac.Mul<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceDiv(tac: Tac.Div<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceConcat(tac: Tac.Concat<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            left = getOrSame(map, tac.left),
            right = getOrSame(map, tac.right)
        ).wrap()
    }

    private fun replaceNeg(tac: Tac.Neg<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            term = getOrSame(map, tac.term),
        ).wrap()
    }

    private fun replaceNot(tac: Tac.Not<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            term = getOrSame(map, tac.term),
        ).wrap()
    }

    private fun replaceShow(tac: Tac.Show<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Op.OfTac<Warm> {
        val map = replacementMap(tac.wrap(), replacand, newTerms)

        return tac.copy(
            to = getOrSame(map, tac.to),
            term = getOrSame(map, tac.term),
        ).wrap()
    }

    private fun replacementMap(op: Op<Warm>, replacand: List<Warm>, newTerms: List<Warm>): Map<Warm, Warm> {
        val oldTerms = replacand.filter { it in op.allTerms() }

        return (oldTerms zip newTerms).toMap()
    }

    private fun getOrSame(map: Map<Warm, Warm>, key: Warm): Warm {
        return map[key] ?: key
    }
}