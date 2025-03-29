package type.chance.repr

/**
 * Mandatory operations for any kind of chance representation.
 */
interface Chanceful<T> {
    // is this the right way of denoting a `Self` type?
    fun map(to: (T) -> T): Chanceful<T>
}