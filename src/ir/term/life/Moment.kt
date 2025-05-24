package ir.term.life

/**
 * A [Moment] is simply an integer that is intended to represent the index of a certain instruction in a [Block].
 * The index is 0-indexed.
 *
 * For example, if we refer to “moment `2`” of this basic block:
 *
 * ```
 * L0:
 *   t0 = 1
 *   t1 = 2
 *   t2 = t0 + t1
 * ```
 *
 * Then “moment `2`” refers to this instruction:
 *
 * ```
 * t2 = t0 + t1
 * ```
 */
typealias Moment = Int

