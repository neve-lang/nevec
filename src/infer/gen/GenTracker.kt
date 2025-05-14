package infer.gen

import type.gen.Free

/**
 * Tracks generic-like types, such as the next ID for the next [Free] type variable.
 */
class GenTracker {
    private var nextQuant = 0
    private var nextFree = 0
    private var freeLevel = 0

    /**
     * “Orders” [n] generic types.
     *
     * @return a [GenOrder] of size [n].
     */
    fun order(n: Int): GenOrder {
        return GenOrder(n, this)
    }

    fun newFree(): Free {
        return Free(nextFree, freeLevel).also {
            nextFree++
        }
    }
}