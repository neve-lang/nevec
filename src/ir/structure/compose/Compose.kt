package ir.structure.compose

import ir.structure.op.Op
import ir.term.warm.Warm
import ir.term.warm.Term

/**
 * Helper data class that simplifies the process of creating new IR [Blocks][ir.structure.block].
 *
 * It works very similarly to a wrapper around a list of IR [Ops][Op].
 */
data class Compose(private val ops: List<Op<Warm>>) {
    companion object {
        /**
         * @return A fresh new [Compose] with the following properties:
         *
         * - An empty list of [ops].
         */
        fun new(): Compose {
            return Compose(emptyList())
        }

        /**
         * @return A fresh new [Compose] which contains a single element—[op].
         */
        fun single(op: Op<Warm>): Compose {
            return Compose(listOf(op))
        }
    }

    /**
     * @return A new [Compose] data class with a new [Op] produced by the callback, where `this` provides the
     * last [Term] produced by the last [Op] as well.
     */
    fun withLast(callback: (Term) -> Op<Warm>): Compose {
        return then { callback(term()) }
    }

    /**
     * @return A new [Compose] data class with a new [Op] produced by the [callback].
     */
    fun then(callback: () -> Op<Warm>): Compose {
        return compose(callback)
    }

    /**
     * @return A new [Junction] containing both [Composes][Compose].
     */
    fun join(other: Compose): Junction {
        return Junction.from(this, other)
    }

    /**
     * @return A new [Viewing] containing both [Composes][Compose].
     */
    fun viewing(other: Compose): Viewing {
        return Viewing(
            listOf(this),
            listOf(other)
        )
    }

    /**
     * @return A new [Compose] data class that merges `this` and [other]’s [Ops][Op].
     *
     * Please note that the order matters—the left [Compose]’s Ops will come **after** those of the right operand.
     */
    fun merge(other: Compose): Compose {
        return Compose(
            ops + other.ops
        )
    }

    /**
     * @return `this` list of [Ops][Op].
     */
    fun ops(): List<Op<Warm>> {
        return ops
    }

    /**
     * @return The [term][Op.term] of the last [Op] in the list of [ops].
     */
    fun term(): Term {
        return ops.last().term()
    }

    private fun compose(callback: () -> Op<Warm>): Compose {
        return Compose(
            ops = ops + callback()
        )
    }
}