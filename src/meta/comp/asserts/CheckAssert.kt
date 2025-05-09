package meta.comp.asserts

/**
 * Provides a [checkFor] method that is [meta assertion][MetaAssert]-specific.
 */
interface CheckAssert<T> {
    fun checkFor(some: T): Boolean
}