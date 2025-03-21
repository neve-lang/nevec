package err.line

import err.note.Note
import file.span.Loc
import file.span.LocBuilder
import file.span.indexable
import util.extension.map

/**
 * A disguised [Line] builder tailored specifically for fix suggestions.
 *
 * @property originalLine The original unmodified line.
 * @property fix The replacement you want to make at [fixLoc].
 * @property msg The message that goes under the fix [Note].
 * @property header An optional header message.
 * @property insert Whether the [fix] should be *inserted* at [fixLoc] or should be treated as a replacement at [fixLoc].
 */
class Suggestion(loc: Loc) {
    companion object {
        fun builder(loc: Loc) = Suggestion(loc)
    }

    private var fixLoc = loc

    private var originalLine: String? = null
    private var fix: String? = null
    private var msg: String? = null
    private var header: String? = null
    private var insert: Boolean = false

    fun build(): Line {
        require(originalLine != null) { "An original line must be given" }
        require(fix != null) { "A fix must be given" }
        require(msg != null) { "A message must be given" }

        val modifiedLine = modify()
        val modifiedLoc = LocBuilder.from(fixLoc).len(fix!!.length.toUInt()).build()
        val notes = listOf(Note.fix(modifiedLoc, msg!!))

        return Line.builder(modifiedLoc).withLine(modifiedLine).add(notes).header(header).build()
    }

    private fun modify(): String {
        val (from, to) = fixLoc.extremes().map(UInt::indexable)

        if (from >= originalLine!!.length) {
            return originalLine + fix!!
        }

        return if (insert) {
            originalLine!!.replaceRange(from, from, fix!!)
        } else {
            originalLine!!.replaceRange(from, to, fix!!)
        }
    }

    fun withOriginal(line: String) = apply { this.originalLine = line }

    fun withMsg(msg: String) = apply { this.msg = msg }

    fun fix(fix: String) = apply { this.fix = fix }

    fun header(msg: String?) = apply { this.header = msg }

    fun insert(insert: Boolean = true) = apply { this.insert = insert }
}