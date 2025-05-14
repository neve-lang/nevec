package type.rec.field

/**
 * Groups multiple record [Field] classes together.
 *
 * @see Field
 * @see type.rec.Rec
 */
class Fields(vararg fields: Field) {
    private val fields = listOf(*fields)

    /**
     * @return The list of [Field] data classes contained in `this`.
     */
    fun themselves(): List<Field> {
        return fields
    }
}