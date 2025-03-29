package type.chance.repr.num

data class NumRange(val min: Edge, val max: Edge) {
    companion object {
        fun real() = NumRange(
            Edge.Excl(Double.NEGATIVE_INFINITY), Edge.Excl(Double.POSITIVE_INFINITY)
        )
    }

    fun includes(some: Double) = !min.isLarger(than = some) && max.isLarger(than = some)

    fun map(to: (Double) -> Double) = NumRange(min.map(to), max.map(to))
}