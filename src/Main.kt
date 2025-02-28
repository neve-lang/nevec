import cli.CliArgs
import cli.Options
import err.line.Line
import err.msg.Msg
import err.msg.MsgKind
import err.note.Note
import err.write.Out
import info.span.Loc

fun main(args: Array<String>) {
    val (file, cliOptions) = try {
        CliArgs.parse(args)
    } catch (e: IllegalArgumentException) {
        println(e.message)
        return
    }

    println("Running with $file!!")

    if (cliOptions.isEnabled(Options.NO_OPT)) {
        println("Optimizations disabled.")
    }

    val original = "  let x = not 10"
    val previous = "fun main"

    val notes = listOf(
        Note.info(Loc(3u, 2u, 5u), "declaring x"),
        Note.fix(Loc(15u, 2u, 2u), "make this a bool"),
        Note.err(Loc(11u, 2u, 3u), "type mismatch")
    )

    val line = Line.builder(Loc(4u, 2u, 4u)).add(notes).header("silly made-up error msg").withLine(original)
        .withPrevious(previous).build()

    val otherNotes = listOf(Note.info(Loc(5u, 1u, 4u), "function declared here"))
    val otherLine = Line.builder(Loc(5u, 1u, 4u)).add(otherNotes).withLine(previous).build()

    val msg =
        Msg.builder().filename(file).msg("okay here's the error").loc(Loc.onLine(2u)).lines(listOf(line, otherLine))
            .kind(MsgKind.WARN).build()

    val out = Out(3)
    msg.emit(out)
}