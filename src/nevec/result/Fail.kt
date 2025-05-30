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
     * Denotes a failure that occurred when the dependency graph was still being built.
     */
    STRUCTURAL,

    /**
     * Denotes a failure that occurred during the parsing stage—i.e., a syntax error.
     */
    PARSE,

    /**
     * Denotes a failure that occurred during the checking phase—this implies, semantic resolving, type checking, etc.
     */
    CHECK;

    /**
     * @return A [Fail] enum variant wrapped in an [Aftermath.OfFail].
     *
     * @see Aftermath
     */
    fun <T> wrap(): Aftermath.OfFail<T> {
        return Aftermath.OfFail(this)
    }
}