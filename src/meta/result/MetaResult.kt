package meta.result

import meta.Meta
import meta.fail.MetaFail

/**
 * Works similarly to a `Result` monad.
 *
 * [MetaResult] is used to indicate whether producing a [Meta] was successful or failed.
 *
 * @see Meta
 */
sealed class MetaResult {
    /**
     * Represents a successfully produced [Meta] data class.
     *
     * @property meta The [Meta] itself.
     */
    data class Success(val meta: Meta) : MetaResult()

    /**
     * Represents an unsuccessful attempt at producing a [Meta] data class.
     *
     * @property reason The kind of [MetaFail] that occurred.
     */
    data class Fail(val reason: MetaFail) : MetaResult()

    /**
     * @param fallback The fallback [Meta] to be returned when `this` is [Fail].
     *
     * Takes in a [fallback] param of type [Meta] that is returned when `this` is [Fail].
     */
    fun or(fallback: Meta) = when (this) {
        is Success -> meta
        is Fail -> fallback
    }

    /**
     * @return
     * - If both are [Fail], the [other] fail,
     * - If one is [Fail] and another [Success], the [Success],
     * - A sum of both [Success] otherwise.
     */
    operator fun plus(other: MetaResult) = when {
        this is Fail && other is Fail -> this
        this is Success && other is Success -> (meta + other.meta).wrap()
        else -> pickSuccess(this, other)
    }

    /**
     * @return If this is a [Success], whether the [Meta] itself [is empty][Meta.isEmpty]; `false` otherwise.
     */
    fun isEmpty() = when (this) {
        is Success -> meta.isEmpty()
        is Fail -> false
    }

    /**
     * @return If this is a [Fail], whether `this` is a [MetaFail.Dummy].
     *
     * @see MetaFail.Dummy
     */
    fun isDummy() = when (this) {
        is Success -> false
        is Fail -> reason is MetaFail.Dummy
    }

    private fun pickSuccess(a: MetaResult, b: MetaResult): MetaResult {
        return if (a is Success)
            a
        else
            b
    }
}