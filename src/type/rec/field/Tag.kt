package type.rec.field

/**
 * Field tags, such as whether a certain field is open, etc.
 */
enum class Tag {
    /**
     * Denotes whether a field may be accessed from anywhere.
     *
     * If not, it may only be accessed via `self`.
     *
     * NOTE: this might be subject to change in the future, i.e. `open` might become
     * the default or `open` will allow restrictions (i.e. `open(a) fun` which only allows
     * a function to be accessed from `self`, this module, or module `a`).
     */
    OPEN,

    /**
     * Denotes a field that is managed in some special way that the user may not access.
     *
     * An example of an `alien` field could be:
     *
     * ```
     * open rec File
     *   alien filename Str
     *   -- ...
     * end
     * ```
     *
     * If a `rec` has one or more `alien` fields, the `rec` cannot be created using a regular constructor.
     *
     * ```
     * -- doesn’t work:
     * let file = File with
     *   filename = "..."
     * end
     * ```
     */
    ALIEN
}