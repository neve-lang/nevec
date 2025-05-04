package meta.target

/**
 * Provides an [appliesTo] method that allows meta components to check whether they apply to the given target.
 *
 * @see meta.asserts.MetaAssert
 */
interface AppliesTo {
    /**
     * @param target The target in question.
     *
     * @return Whether the implementor meta component applies to the target in question.
     */
    fun appliesTo(target: Target): Boolean
}