package err.help

import err.line.Line
import err.line.Suggestion
import file.span.Loc

/**
 * Helper object: makes the process of building suggestions easier.
 */
object Suggest {
    fun replacing(at: Loc, with: String, saying: String? = null): Line {
        val msg = "replaced ${at.lexeme()} with $with"
        return makeSuggestion(at, msg, with, saying)
    }

    fun adding(what: String, at: Loc, saying: String? = null): Line {
        val msg = "added '$what'"
        return makeSuggestion(at, msg, what, saying, insert = true)
    }

    private fun makeSuggestion(loc: Loc, msg: String, fix: String, header: String?, insert: Boolean = false): Line {
        return Suggestion.builder(loc).withOriginal(loc.line()).fix(fix).withMsg(msg).header(header).insert(insert)
            .build()
    }
}