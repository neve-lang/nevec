package err.pre

import err.help.Lines
import err.msg.Msg
import err.note.Note
import err.report.Report
import file.span.Loc
import java.lang.ProcessHandle.Info

/**
 * Provides a collection of pre-made error messages for common cases.
 */
object PreMsgs {
    /**
     * @return A common error message when a [meta component][meta.comp.MetaComp] requires an input, but none was
     * given.
     *
     * @param loc The location of the meta component.
     *
     * @see meta.comp.MetaComp
     */
    fun missingMetaInput(loc: Loc): Msg {
        return Report.err(loc, "missing meta component input").lines(
            Lines.of(Note.err(loc, "expected some input here"))
        ).build()
    }

    /**
     * @return A common error message when a [meta component][meta.comp.MetaComp] requires no input, but some was
     * given.
     *
     * @param loc The location of the meta component.
     *
     * @see meta.comp.MetaComp
     */
    fun unexpectedMetaInput(loc: Loc): Msg {
        return Report.err(loc, "unexpected meta component input").lines(
            Lines.of(Note.err(loc, "this meta component takes in no input"))
        ).build()
    }
}