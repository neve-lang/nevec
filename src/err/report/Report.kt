package err.report

import err.msg.Msg
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
    lateinit var OUT: Out

    fun setup(maxLine: Int) {
        OUT = Out(maxLine)
    }

    fun fileErr(filename: String) {
        Write.paintedIn(Color.RED).saying(" × ").then().saying("could not read '$filename'").print(Out.fatal())
    }

    fun err(loc: Loc, msg: String) = Msg.builder().kind(MsgKind.ERR).msg(msg).loc(loc).filename(Src.FILENAME)
}