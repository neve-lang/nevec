package chance.repr.str

import chance.repr.Chanceful

data class StrChances(val possibilities: HashSet<String> = HashSet()) : Chanceful<String> {
    companion object {
        fun from(vararg possibilities: String) = StrChances(possibilities.toHashSet())
    }

    override fun includes(some: String) = possibilities.contains(some)

    override fun map(to: (String) -> String) = StrChances(possibilities.map(to).toHashSet())

    override fun equals(other: Any?) = other is StrChances && possibilities == other.possibilities

    override fun hashCode() = possibilities.hashCode()
}