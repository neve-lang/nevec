package type.chance.repr

class ChanceRepr<T>(val chances: List<T>) : Chanceful<T> {
    override fun map(to: (T) -> T) = ChanceRepr(chances.map(to))
}