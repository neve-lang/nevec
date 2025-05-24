package ir.structure.compose

import ir.structure.op.Op
import ir.term.warm.Warm
import ir.term.warm.Term

/**
 * Helper class that simplifies the process of building more complex sets of IR [Ops][ir.structure.op.Op].
 *
 * # Motivation
 *
 * Sometimes, simply using a [Junction] isn’t enough—they’re powerful, but they still present some limitations in
 * certain scenarios.
 *
 * For example, imagine we wanted to emit the IR for this expression:
 *
 * ```
 * ["Hello, ": 10, "world!": 20]
 * ```
 *
 * Without table propagation, our goal would be to generate an IR that looks more or less like this:
 *
 * ```
 * t0 = "Hello, "
 * t1 = "world!"
 * t2 = 10
 * t3 = 20
 * t4 = [:]
 * t4[t0] = t2
 * t4[t1] = t3
 * ```
 *
 * However, using only [Junction], this proves to be difficult and unergonomic—we have two lists of [Composes][Compose],
 * not a single one, so it seems like it’s not very well suited for this specific task.
 *
 * And this is where [Zippings][Zipping] come in: they allow us to manipulate *two lists of [Composes][Compose]* and
 * emit IR for them **pair by pair**, where a **pair** groups to two elements from the both lists that have the same
 * index; just like a [zip].
 */
data class Zipping(private val a: List<Compose>, private val b: List<Compose>) {
    /**
     * @return A new [Compose] built from the **[merged][Compose.merge] results** of the [callback] call applied to
     * the [Zipping], *plus* the original [Composes][Compose].
     *
     * @throws IllegalArgumentException If either list is empty.
     */
    fun each(callback: (Pair<Term, Term>) -> Op<Warm>): Connection {
        val zips = a.map(Compose::term) zip b.map(Compose::term)

        return Connection(
            previous(),
            product(zips, callback)
        )
    }

    private fun product(zips: List<Pair<Term, Term>>, callback: (Pair<Term, Term>) -> Op<Warm>): Compose {
        return zips
            .map(callback)
            .map(Compose::single)
            .reduceOrNull(Compose::merge)
            ?: Compose.new()
    }

    private fun previous(): Compose {
        val reducedA = a.reduceOrNull(Compose::merge) ?: Compose.new()
        val reducedB = b.reduceOrNull(Compose::merge) ?: Compose.new()

        return reducedA.merge(reducedB)
    }
}