import cli.CliArgs
import cli.Options
import err.line.Line
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

    val out = Out(3)
    line.emit(out)
}