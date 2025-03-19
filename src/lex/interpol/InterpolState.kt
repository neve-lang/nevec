package lex.interpol

/**
 * Abstracts away state management for string interpolation in a single-pass Lexer.
 */
class InterpolState {
    companion object {
        private const val MAX_INTERPOL_DEPTH = 255
    }

    private enum class State {
        AFTER_INTERPOL, NORMAL,
    }

    private var state = State.NORMAL
    private var depth = 0

    fun inInterpol() = depth > 0

    /**
     * Returns whether the LexState was in a string interpolation.
     *
     * *Implicitly updates the state* after being called.
     */
    fun wasInInterpol(): Boolean {
        val previousState = state
        update()

        return previousState == State.AFTER_INTERPOL
    }

    /**
     * Returns whether the LexState was in a string interpolation.
     *
     * *Does not* implicitly update the state after being called.
     */
    fun isAfterInterpol() = state == State.AFTER_INTERPOL

    fun deepenInterpol() {
        if (depth >= MAX_INTERPOL_DEPTH) {
            throw InterpolTooDeepException()
        }

        depth++
    }

    fun endInterpol() {
        state = State.AFTER_INTERPOL
        depth--
    }

    private fun update() {
        if (state != State.AFTER_INTERPOL) {
            return
        }

        state = State.NORMAL
    }
}