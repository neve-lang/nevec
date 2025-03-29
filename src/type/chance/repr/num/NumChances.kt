package type.chance.repr.num

import type.chance.repr.Chanceful

sealed class NumChances : Chanceful<Double> {
    data class OfInt(val chances: List<NumRange>) : NumChances()
    data class OfFloat(val chances: List<NumRange>) : NumChances()

    override fun map(to: (Double) -> Double): NumChances = when (this) {
        is OfInt -> OfInt(chances.map { it.map(to) })
        is OfFloat -> OfFloat(chances.map { it.map(to) })
    }
}