package type.rec.field

/**
 * Field tags, such as whether a certain field is open, etc.
 */
enum class Tag {
    /**
     * Denotes whether a field may be accessed from anywhere.
     *
     * If not, it may only be accessed via `self`.
     */
    OPEN,
    ALIEN
}