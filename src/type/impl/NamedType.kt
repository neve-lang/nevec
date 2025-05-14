package type.impl

/**
 * Provides a [named] method for type data classes.
 */
interface NamedType {
    /**
     * @return the implementor type’s name.
     */
    fun named(): String
}