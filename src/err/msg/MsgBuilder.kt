package err.msg

import err.line.Line
import info.span.Loc

class MsgBuilder {
    var kind: MsgKind? = null

    var filename: String? = null
    var msg: String? = null
    var loc: Loc? = null

    var lines: List<Line>? = null

    fun kind(kind: MsgKind) = apply { this.kind = kind }
    fun filename(filename: String) = apply { this.filename = filename }
    fun msg(msg: String) = apply { this.msg = msg }
    fun loc(loc: Loc) = apply { this.loc = loc }
    fun lines(lines: List<Line>) = apply { this.lines = lines }

    fun build(): Msg {
        // this could definitely be better, but, hey, we’ll play by Kotlin’s rules
        // something awesome about Neve is that it allows you to do these checks
        // at *compile time*!!  yes, seriously!!
        // take a look at this:
        //
        // fun build
        // where
        //   self.lines? and self.kind? and self.filename? and
        //   self.msg? and self.loc?
        // end
        //   Line with
        //     kind
        //     lines
        //     ...
        //   end
        // end
        //
        // then, when using this function:
        //
        // let msg = Msg.builder.build
        //                       ----- cannot prove `where` clause for function `build`
        //
        // however, if you *do* specify the fields, Neve’s *function implications* will be
        // able to prove it without requiring an `if` check!!
        require(lines != null) { "Lines must be provided" }
        require(kind != null) { "A kind must be provided" }
        require(filename != null) { "A filename must be provided" }
        require(msg != null) { "A message must be provided" }
        require(loc != null) { "A Loc must be provided" }

        return Msg(kind!!, filename!!, msg!!, loc!!, lines!!)
    }
}