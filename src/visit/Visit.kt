package visit

/**
 * Provides a [visit] method for visitor patterns.
 *
 * @param T The type being visited.
 * @param U The type being output.
 */
interface Visit<T, U> {
    /**
     * Visits [what].
     *
     * @param what The value being visited.
     *
     * @return [U].
     */
    fun visit(what: T): U
}