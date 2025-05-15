package meta.comp.asserts.outcome

/**
 * Represents a possible outcome of a [meta assertion][meta.comp.asserts.MetaAssert], which can be one of three
 * outcomes:
 *
 * - [SUCCESS]—The assertion was successful.
 * - [FAIL]—The assertion was *not* successful.
 * - [MISSING_INPUT]—The assertion required some input but none was given.
 */
enum class AssertOutcome {
    /**
     * Denotes a successful outcome for a [meta assertion][meta.comp.asserts.MetaAssert].
     */
    SUCCESS,

    /**
     * Denotes a non-successful outcome for a [meta assertion][meta.comp.asserts.MetaAssert]
     */
    FAIL,

    /**
     * This outcome is given when the [meta assertion][meta.comp.asserts.MetaAssert] expected some input but the
     * user gave none.
     */
    MISSING_INPUT;

    companion object {
        /**
         * @return An [AssertOutcome] variant based on some [Boolean] value [bool].  That is:
         *
         * - If `true` is given, [SUCCESS] is returned.
         * - If `false` is given, [FAIL] is returned.
         */
        fun basedOn(bool: Boolean): AssertOutcome {
            return if (bool)
                SUCCESS
            else
                FAIL
        }
    }

}