package opt.structure.id

/**
 * Represents an optimization pass’s ID.
 *
 * For each distinct optimization pass, there exists a distinct [OptId].
 *
 * [OptIds][OptId] allow us to disable certain optimization passes.
 */
enum class OptId {
    /**
     * The constant-folding optimization pass ID.
     */
    CONST_FOLD
}