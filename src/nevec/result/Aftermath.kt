package nevec.result

/**
 * Denotes whether a compilation stage was successful or failed.
 *
 * @param T The kind of value that should be returned when the [Aftermath] is successful.
 *
 * In [Nevec][nevec.Nevec], [Aftermaths][Aftermath] are passed around in between [Stages][stage.Stage].  If a stage
 * fails, the compilation attempt is abandoned early.
 */
sealed class Aftermath<T> {
    /**
     * Represents a successful compilation stage.
     *
     * @property result The result of the successful compilation stage.
     */
    data class Success<T>(val result: T) : Aftermath<T>()

    /**
     * Represents an unsuccessful compilation stage.
     *
     * @property fail The stage at which the failure occurred.
     */
    data class OfFail<T>(val fail: Fail) : Aftermath<T>()

    /**
     * @return Whether `this` [Aftermath] is [OfFail].
     */
    fun isFail(): Boolean {
        return this is OfFail
    }

    /**
     * @return If `this` is [Success], simply `this`, but otherwise, `null`.
     */
    fun cure() = when (this) {
        is Success -> this
        is OfFail -> null
    }

    /**
     * @return A new [Aftermath] containing the given [value] if `this` is [Success], or simply a copy of `this` if
     * `this` is [OfFail].
     */
    fun <T> into(value: T) = when (this) {
        is Success -> Success(value)
        is OfFail -> OfFail(fail)
    }
}