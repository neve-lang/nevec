package parse.meta

/**
 * Simple data class that abstracts away the detection of input for meta components, as certain meta components
 * require input, whereas others don’t.
 *
 * @see meta.comp.MetaComp
 */
sealed class PossibleInput {
    /**
     * Represents input that was explicitly given by the user.
     */
    data object Given : PossibleInput()

    /**
     * Represents the case where the user does not provide any input.
     */
    data object Missing : PossibleInput()

    /**
     * @return Whether `this` is [Given].
     */
    fun isGiven(): Boolean {
        return this is Given
    }
}