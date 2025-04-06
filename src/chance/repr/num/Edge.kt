package chance.repr.num

/**
 * An edge in a [NumRange].  An edge may be:
 *
 * - [Incl]: inclusive, i.e. `[2, 4]` has only inclusive edges
 * - [Excl]: exclusive, i.e. `]5, 10[` has only exclusive edges.
 *
 * Edge values are *always* represented using [Double], no matter the type.
 *
 * Representing an edge's value with a generic type within bounds was attempted:
 *
 * ```kt
 * sealed class Edge
 *     where T : Number,
 *           T : Comparable<T>
 * ```
 *
 * However, this was too limited, as the [Number] interfaces provides only [toString], [toDouble], etc. methods, and
 * did not provide arithmetic methods such as [unaryMinus].
 */
sealed class Edge {
    data class Incl(val value: Double) : Edge()
    data class Excl(val value: Double) : Edge()

    fun isLarger(than: Double) = when (this) {
        is Incl -> value >= than
        is Excl -> value > than
    }

    fun isLarger(than: Edge) = roughValue() >= than.roughValue()

    fun isSmaller(than: Double) = when (this) {
        is Incl -> value <= than
        is Excl -> value < than
    }

    fun isSmaller(than: Edge) = roughValue() <= than.roughValue()

    private fun roughValue() = when (this) {
        is Incl -> value

        // -0.00000000001 to approximate the exclusion
        // not using Double.MIN_VALUE because
        // a < a - Double.MIN_VALUE
        // evaluates to false for some reason.
        is Excl -> value - 0.00000000001
    }

    fun map(to: (Double) -> Double) = when (this) {
        is Incl -> Incl(to(value))
        is Excl -> Excl(to(value))
    }

    override fun equals(other: Any?) = when {
        this is Incl && other is Incl -> value == other.value
        this is Excl && other is Excl -> value == other.value
        else -> false
    }
}