package chance.repr.str

import chance.repr.Chanceful

data class StrChances(val possibilities: HashSet<String> = HashSet()) : Chanceful<String> {
    override fun map(to: (String) -> String) = StrChances(possibilities.map(to).toHashSet())

    override fun equals(other: Any?) = other is StrChances && possibilities == other.possibilities

    override fun hashCode() = possibilities.hashCode()
}