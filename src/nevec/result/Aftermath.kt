package nevec.result

/**
 * Denotes whether compilation was successful or failed.
 */
sealed class Aftermath {
    /**
     * Represents a successful compilation.
     */
    data object Success : Aftermath()

    /**
     * Represents an unsuccessful compilation.
     */
    data class OfFail(val fail: Fail) : Aftermath()
}