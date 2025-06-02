package ir.data.`fun`

import ir.data.change.FunChange
import ir.data.term.TermData
import ir.structure.block.Block
import ir.structure.consts.IrConst
import ir.term.TermLike

/**
 * Describes IR function statistics—such as which constants are used and by what terms.
 *
 * @property constDefMap Maps an [IrConst] to a list of terms.  Each distinct [IrConst] key is unique—there won’t be two
 * IrConsts of the same value stored.
 */
data class FunData<T : TermLike>(
    val constDefMap: Map<IrConst, List<T>>,
    val termData: TermData<T>
) {
    companion object {
        /**
         * @return A new [FunData] with the following properties:
         *
         * - An empty map for [constDefMap]
         * - A fresh new [TermData] for [termData]
         */
        fun <T : TermLike> new(): FunData<T> {
            return FunData(
                constDefMap = emptyMap(),
                termData = TermData.new()
            )
        }

        /**
         * @return A new [FunData] whose properties were derived from the list of [Blocks][Block].
         */
        fun <T : TermLike> from(blocks: List<Block<T>>): FunData<T> {
            val ops = blocks.flatMap { it.ops }
            val changes = ops.flatMap { FunChange.deriveFrom(it) }

            return updateAll(changes)
        }

        private fun <T : TermLike> updateAll(changes: List<FunChange<T>>): FunData<T> {
            return if (changes.isEmpty())
                new()
            else
                changes.first().applyTo(updateAll(changes.drop(1)))
        }
    }

    /**
     * @return The [List] of [T] that maps the given [const] in [constDefMap], or `null` if the entry does not exist.
     */
    fun termsUsing(const: IrConst): List<T>? {
        return constDefMap[const]
    }

    /**
     * @return A new [FunData] that merges the properties of both operands.
     */
    operator fun plus(other: FunData<T>): FunData<T> {
        return FunData(
            constDefMap + other.constDefMap,
            termData + other.termData
        )
    }
}