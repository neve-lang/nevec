package lex.relex

import tok.Tok
import tok.TokKind

/**
 * Functions as a peephole--takes two [Tok]s and merges them into one if possible.
 *
 * # Example
 *
 * ```
 * TokKind.NOT, TokKind.IN -> TokKind.NOT_IN
 * TokKind.IS, TokKind.NOT -> TokKind.IS_NOT
 * ```
 */
class Relex {
    private var peephole = mutableListOf<Tok>()

    fun consume(predicate: () -> Tok): Tok {
        pushUntil(::canFlush, predicate)
        return flush()
    }

    private fun pushUntil(predicate: () -> Boolean, what: () -> Tok) {
        if (predicate()) {
            return
        }

        push(what())
        pushUntil(predicate, what)
    }

    private fun push(tok: Tok) {
        peephole.add(tok)
    }

    private fun flush(): Tok {
        val (left, right) = Pair(peephole[0], peephole[1])
        drop()

        return merge(left, right)?.also { peephole.clear() } ?: left
    }

    private fun drop() {
        peephole = peephole.takeLast(1).toMutableList()
    }

    private fun merge(left: Tok, right: Tok) = when (Pair(left.kind, right.kind)) {
        Pair(TokKind.NOT, TokKind.IN) -> Combine(left, right).into(TokKind.NOT_IN)
        Pair(TokKind.IS, TokKind.NOT) -> Combine(left, right).into(TokKind.IS_NOT)
        else -> null
    }

    private fun canFlush() = peephole.size == 2
}