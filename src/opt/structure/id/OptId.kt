package opt.structure.id

import cli.CliOptions
import cli.Options.*

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
    CONST_FOLD,

    /**
     * The table propagation optimization pass ID.
     *
     * @see opt.passes.TablePropagation
     */
    TABLE_PROPAGATION,

    /**
     * The dead-term elimination optimization pass ID.
     */
    DEAD_TERM_ELIM;

    companion object {
        /**
         * Maps each [Options][cli.Options] to a set of [OptIds][OptId] that are to be excluded if the CLI option is
         * enabled.
         */
        private val DISABLING_FLAGS = mapOf(
            NO_OPT to listOf(CONST_FOLD),
            OPT_NO_CONST_FOLD to listOf(CONST_FOLD),
            OPT_NO_TABLE_PROPAGATION to listOf(TABLE_PROPAGATION)
        )

        /**
         * @return A list of [OptIds][OptId] that are derived from the given [CliOptions] enabled.
         *
         * For example:
         *
         * - If the user has the `--no-opt` flag, most optimizations are disabled.
         */
        fun fromCliOptions(options: CliOptions): List<OptId> {
            val disabled = options.enabledOptions().mapNotNull {
                DISABLING_FLAGS[it]
            }.flatten()

            return OptId.entries - disabled.toSet()
        }
    }
}