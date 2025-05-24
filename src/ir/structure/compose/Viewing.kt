package ir.structure.compose

import ir.structure.op.Op
import ir.term.warm.Warm
import ir.term.warm.Term

/**
 * Helper class that simplifies the process of building an IR [Op] from the information of another.
 *
 * # Motivation
 *
 * [Junction] is powerful, but its behavior can sometimes be undesirable—it *always* reduces the composes given to
 * it together.
 *
 * For this reason, [Viewing] serves a very similar purpose, but it does *not* reduce the undesired [Composes][Compose]
 * together.
 *
 * @param desired The list of [Composes][Compose] that are desired.  It must not be empty.
 * @param viewing The list of [Composes][Compose] that are only being viewed.  It must also not be empty.
 */
data class Viewing(val desired: List<Compose>, val viewing: List<Compose>) {
    /**
     * @param callback The given callback.  The **merged list** of [desired] and [viewing] are given to the callback.
     *
     * @return A [Connection] bringing together the **reduced** [desired] list of [Composes][Compose], and
     * the [Compose] produced by the given callback.
     *
     * @throws IllegalArgumentException If either list is empty.
     */
    fun then(callback: (List<Term>) -> Op<Warm>): Connection {
        require(desired.isNotEmpty() && viewing.isNotEmpty()) {
            "`Viewing` requires both lists given to it to not be empty."
        }

        val reduced = desired.reduce(Compose::merge)
        val produced = Compose.single(
            callback((desired + viewing).map(Compose::term))
        )

        return Connection(reduced, produced)
    }
}