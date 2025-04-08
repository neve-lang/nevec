package chance.repr.num

import chance.repr.Chanceful

data class NumChances(val chances: List<NumRange>) : Chanceful<Double> {
    companion object {
        fun real() = of(NumRange.real())

        fun of(limits: IntRange) = NumChances(listOf(
            NumRange.of(limits)
        ))

        fun of(range: NumRange) = NumChances(listOf(range))
    }

    override infix fun includes(some: Double) = chances.any { it.includes(some) }

    infix fun includes(some: NumChances) = chances.any { a -> some.chances.all { b -> a.includes(b) } }

    override fun map(to: (Double) -> Double) = NumChances(chances.map { it.map(to) })

    override fun equals(other: Any?) = other is NumChances && chances == other.chances

    override fun hashCode() = chances.hashCode()
}
