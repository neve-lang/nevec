package err.write

/**
 * Simple object to make the code look just the tiniest bit more readable.  It's not absolutely necessary, but I like it.
 *
 * ```
 * Write.paintedIn(Color.BLUE)...
 * ```
 */
object Write {
    fun paintedIn(color: Color) = OutputBuilder().paintedIn(color)
}