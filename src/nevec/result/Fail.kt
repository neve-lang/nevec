package nevec.result

/**
 * A compiler failure.
 */
enum class Fail {
    /**
     * Denotes a CLI error—such as when the user does not provide a file name or uses the `nevec` command improperly.
     */
    CLI,

    /**
     * Denotes an IO failure, such as an invalid file name.
     */
    IO,

    /**
     * Denotes a failure that happened at the compilation stage—a syntax error, a semantic error, a type error, etc.
     */
    COMPILE;

    /**
     * @return A [Fail] enum variant wrapped in an [Aftermath.OfFail].
     *
     * @see Aftermath
     */
    fun wrap(): Aftermath.OfFail {
        return Aftermath.OfFail(this)
    }
}