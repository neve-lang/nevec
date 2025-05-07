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
}