package ir.data.term

import ir.structure.op.Op
import ir.term.TermLike

/**
 * Stores “statistics” for IR terms, such as:
 *
 * - Their IR definition; as an IR [Op]
 * - Their list of usages—the IR [Op]s that reference them.  Definitions do not count as a usage.
 */
data class Stats<T : TermLike>(
    val def: Op<T>? = null,
    val uses: List<Op<T>> = emptyList()
)