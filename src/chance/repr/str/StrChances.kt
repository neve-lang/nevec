package chance.repr.str

import chance.repr.Chanceful

sealed class StrChances : Chanceful<String> {
    data class Limited(val possibilities: HashSet<String>) : StrChances()
    data object Every : StrChances()

    companion object {
        fun from(vararg possibilities: String) = Limited(possibilities.toHashSet())
    }

    override fun includes(some: String) = when (this) {
        is Limited -> possibilities.contains(some)
        is Every -> true
    }

    override fun map(to: (String) -> String): StrChances = when (this) {
        is Limited -> Limited(possibilities.map(to).toHashSet())
        is Every -> this
    }

    override fun equals(other: Any?) = when {
        this is Every && other is Every -> true
        this is Every || other is Every -> false
        this is Limited && other is Limited -> possibilities == other.possibilities
        else -> false
    }

    override fun hashCode() = javaClass.hashCode()
}