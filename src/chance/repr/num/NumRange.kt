package chance.repr.num

data class NumRange(val min: Edge, val max: Edge) {
    companion object {
        fun real() = NumRange(
            Edge.Excl(Double.NEGATIVE_INFINITY), Edge.Excl(Double.POSITIVE_INFINITY)
        )

        fun single(value: Double) = NumRange(
            Edge.Incl(value), Edge.Incl(value)
        )

        fun of(limits: IntRange) = NumRange(
            Edge.Incl(limits.first.toDouble()), Edge.Incl(limits.last.toDouble())
        )
    }

    infix fun includes(some: Double) = min.isSmaller(than = some) && max.isLarger(than = some)

    infix fun includes(some: NumRange) = min.isSmaller(than = some.min) && max.isLarger(than = some.max)

    fun map(to: (Double) -> Double) = NumRange(min.map(to), max.map(to))

    override fun equals(other: Any?) = other is NumRange && min == other.min && max == other.max
}