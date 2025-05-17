package check.help

import ast.info.Info
import err.help.Lines
import err.note.Note
import err.report.Report
import type.Type

/**
 * Simple helper data class that helps the type checker in its assumptions.
 *
 * It works by receiving a list of infos, and then applying a predicate to their types.  The predicate is often
 * predictable and provided by a method, such as [are].
 *
 * Predicates are composed—this means that methods like [sameType] and [are] can be chained to check multiple
 * properties at once.
 *
 * @property infos The list of [Infos][Info] in question.
 */
data class Assume(
    private val infos: List<Info>,
    private val predicate: (Type) -> Boolean = { true }
) {
    companion object {
        /**
         * @return A new [Assume] data class with all the [infos] given, as a [List].
         */
        fun all(vararg infos: Info): Assume {
            return Assume(infos.toList())
        }

        /**
         * @return A new [Assume] data class all the [infos] given as a [List], with a predicate that determines
         * whether all types in the list of [Infos][Info] have the same type.
         *
         * Comparison is done using [Type.isSame]—however, this is subject to change once we implement
         * [type domains][domain.Domain].
         *
         * @see Type.isSame
         */
        fun sameType(vararg infos: Info): Assume {
            return Assume(
                infos.toList()
            ) { type -> infos.map(Info::type).all { it.isSame(type) } }
        }
    }

    /**
     * @return A new [Assume] with an extra predicate that determines whether all types of [infos] are one of [others].
     * In other words: `forall t in types : t in others`.
     */
    fun are(vararg others: Type): Assume {
        return compose { it in others }
    }

    /**
     * Check the [predicate], outputting an error if one was given.
     *
     * @param notes Additional information for the error message in case of a failure.
     *
     * @return A [Boolean] indicating whether the predicate succeeded or failed.
     */
    fun orFail(vararg notes: Note): Boolean {
        val types = infos.map(Info::type)

        if (!types.all { predicate(it) }) {
            val firstLoc = infos.first().loc()

            Report.err(
                firstLoc,
                msg = "type error"
            ).lines(
                Lines.of(
                    *culpritsAsNotes(),
                    *notes
                )
            ).build().print()

            return false
        }

        return true
    }

    private fun culpritsAsNotes(): Array<Note> {
        val culprits = infos.filterNot { predicate(it.type()) }

        return culprits.map {
            Note.err(it.loc(), msg = it.type().named())
        }.toTypedArray()
    }

    private fun compose(with: (Type) -> Boolean): Assume {
        return Assume(
            infos
        ) { predicate(it) && with(it) }
    }
}