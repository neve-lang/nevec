package visit

interface Visit<T, U> {
    fun visit(what: T): U
}