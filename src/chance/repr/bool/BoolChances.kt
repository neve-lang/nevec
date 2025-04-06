package chance.repr.bool

import chance.repr.Chanceful

enum class BoolChances : Chanceful<Boolean> {
    ALWAYS_TRUE,
    ALWAYS_FALSE,
    EITHER;

    companion object {
        fun from(some: Boolean) = if (some)
            ALWAYS_TRUE
        else
            ALWAYS_FALSE

        fun undecidable() = EITHER
    }

    fun into() = this == ALWAYS_TRUE

    override fun map(to: (Boolean) -> Boolean) = BoolChances.from(to(into()))
}