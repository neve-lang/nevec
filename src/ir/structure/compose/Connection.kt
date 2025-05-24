package ir.structure.compose

/**
 * Helper class that allows more complex combinations of IR [Ops][ir.structure.op.Op] when emitting them, by allowing
 * insertions and splitting.
 *
 * # Motivation
 *
 * [Junctions][Junction] and [Zippings][Zipping] are really useful, but sometimes, they also feel a little rigid.  For
 * example—in order to emit the IR for a table expression node like the following:
 *
 * ```
 * ["Hello, ": 10, "world!": 20]
 * ```
 *
 * [Zipping] allows us to emit the IR for it easily, *except that* we’re missing the IR [Op] that defines `t4`, the
 * term for the table:
 *
 * ```
 * t0 = "Hello, "
 * t1 = "world!"
 * t2 = 10
 * t3 = 20
 * ; where’s t4?
 * t4[t0] = t2
 * t4[t1] = t3
 * ```
 *
 * So instead, we use the [Connection] established by the [Zipping] to insert it right in between, using the
 * [plugInBetween] method.
 */
data class Connection(private val a: Compose, private val b: Compose) {
    /**
     * @return A new [Compose] which simply [merges][Compose.merge] both Composes in the [Connection].
     */
    fun connect(): Compose {
        return a.merge(b)
    }

    /**
     * @return A new [Compose] built from plugging the [given] compose in between both [Composes][Compose] of the
     * [Connection], and merging all three.
     */
    fun plugInBetween(given: Compose): Compose {
        return a.merge(given).merge(b)
    }

    /**
     * @return A new [Compose] built from plugging the [given] compose *before* both [Composes][Compose] of the
     * [Connection], and merging all three.
     */
    fun plugAtFront(given: Compose): Compose {
        return given.merge(a).merge(b)
    }
}