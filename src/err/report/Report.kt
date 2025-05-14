package err.report

import err.msg.Msg
import err.msg.MsgBuilder
import err.msg.MsgKind
import err.write.Color
import err.write.Out
import err.write.Write
import file.contents.Src
import file.span.Loc

/**
 * Simplifies the process of building error [Msg]s by providing the file name and the source lines of the current
 * [file.module.Module].
 */
object Report {
    /**
     * The writer used to output error messages.
     *
     * @see Out
     */
    lateinit var OUT: Out

    /**
     * Sets up the `lateinit var` [OUT] with [maxLine].
     */
    fun setup(maxLine: Int) {
        OUT = Out(maxLine)
    }

    /**
     * Displays a simple error message to the user saying that [filename] could not be read.
     */
    fun cliFileErr(filename: String) {
        Write.paintedIn(Color.RED).saying(" × ").then()
            .saying("could not read '$filename'").print(Out.fatal())
    }

    /**
     * Returns a simple error message **as a [MsgBuilder]**, such that it can be further extended.
     */
    fun err(loc: Loc, msg: String): MsgBuilder {
        return Msg.builder().kind(MsgKind.ERR).msg(msg).loc(loc).filename(Src.FILENAME)
    }
}