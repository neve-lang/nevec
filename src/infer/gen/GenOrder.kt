package infer.gen

import type.gen.Free

/**
 * Silly class that takes care of supplying generic types, such as [Quant][type.gen.Quant] types.
 *
 * @property n The number of items to “order.”
 * @property state The previous [GenTracker] state.
 */
data class GenOrder(val n: Int, val state: GenTracker) {
    /**
     * Provides [n] fresh new [Free] types.
     *
     * @return a list of [n] fresh [Free] types.
     */
    fun frees(): List<Free> {
        return (0..<n).map {
            state.newFree()
        }
    }
}