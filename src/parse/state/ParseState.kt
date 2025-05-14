package parse.state

import err.msg.Msg

/**
 * Coordinates the current [Parse][parse.Parse] state.
 *
 * @see parse.Parse
 */
class ParseState {
    /**
     * Whether an error occurred during the parsing phase.
     */
    var hadErr = false

    /**
     * Whether the parser is currently panicking, i.e. skipping through code after an error
     * to avoid cascading ones.
     */
    var isPanicking = false

    /**
     * Shows a compiler [error message][Msg] and marks that an error was reported.
     */
    fun showMsg(msg: Msg) {
       return msg.print().also { markErr() }
    }

    /**
     * Sets [isPanicking] to false.
     */
    fun relax() {
        isPanicking = false
    }

    /**
     * Sets [hadErr] to `true` and starts panicking.
     */
    fun markErr() {
        hadErr = true
        panic()
    }

    private fun panic() {
        isPanicking = true
    }
}