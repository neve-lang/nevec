package ir.structure.compose

import ir.structure.op.Op
import ir.structure.tac.Tac
import ir.term.warm.Warm
import ir.term.warm.Term

/**
 * Represents a junction of multiple [Composes][Compose].
 *
 * @property composes The list of [Composes][Compose].
 *
 * # Motivation
 *
 * It is useful in cases where the last [Ops][Op] of multiple [Composes][Compose] needs to be retrieved in order to
 * build the next.
 *
 * For example, in cases of **binary operations**, consider the following expression:
 *
 * ```
 * (1 + 2) + (3 + 4)
 * ```
 *
 * Let a binary operation’s **prelude** correspond to the list of IR [Tacs][Tac] required before the
 * binary operation itself can be emitted.
 *
 * Given this expression, the IR of the main expression, `(1 + 2) + (3 + 4)`, would look like this:
 *
 * ```
 * t0 = 1
 * t1 = 2
 * t2 = t0 + t1
 * t3 = 3
 * t4 = 4
 * t5 = t3 + t4
 * ```
 *
 * And we want to emit the next instruction, which is the final sum:
 *
 * ```
 * t6 = t2 + t5
 * ```
 *
 * Notice how `t2` and `t5`’s definitions are far from each other?  This is where [Junctions][Junction] come in: they
 * don’t immediately *unify* all [Composes][Compose] into a single one—instead, they are kept separated in a list,
 * and a [then] method is provided to retrieve however many composes are needed for the next [Tac].
 *
 * @see Compose
 */
data class Junction(private val composes: List<Compose>) {
    companion object {
        /**
         * @return A new [Junction] whose [composes] are built from the variadic list of arguments.
         */
        fun from(vararg composes: Compose): Junction {
            return Junction(composes.toList())
        }
    }

    /**
     * @return A new [Junction] with the extra [compose] given.
     */
    fun join(compose: Compose): Junction {
        return Junction(composes + compose)
    }

    /**
     * @param callback The given callback.
     *
     * @return The [List] of [Compose] stored in the [Junction], which is passed to the given [callback].  This result
     * is then used to produce a new [Connection] combining all composes in the junction and *then* the newly produced
     * one.
     *
     * @throws IllegalArgumentException If the list of composes is empty.
     */
    fun then(callback: (List<Term>) -> Op<Warm>): Connection {
        require(composes.isNotEmpty()) {
            "`then` may only be used with non-empty lists of Composes."
        }

        val reduced = composes.reduce(Compose::merge)
        val produced = Compose.single(callback(composes.map(Compose::term)))

        return Connection(reduced, produced)
    }
}