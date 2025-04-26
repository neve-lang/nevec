package lex.interpol

/**
 * Abstracts away state management for string interpolation in a single-pass Lexer.
 */
class InterpolState {
    companion object {
        /**
         * This limit exists because we only have so many registers in the Neve VM.
         */
        private const val MAX_INTERPOL_DEPTH = 255
    }

    /**
     * The Lexer’s interpolation state.
     *
     * It can be either:
     *
     * - `AFTER_INTERPOL`
     * - `NORMAL`
     *
     * Whether the lexer is **currently within an interpolation** is handled by `this` [depth] (private field.)
     */
    private enum class State {
        AFTER_INTERPOL, NORMAL,
    }

    private var state = State.NORMAL
    private var depth = 0

    /**
     * @return whether `this` [depth] is above 0.
     */
    fun isInInterpol(): Boolean {
        return depth > 0
    }

    /**
     * *Implicitly updates the state* after being called.
     *
     * @return whether the LexState was in a string interpolation.
     */
    fun wasInInterpol(): Boolean {
        val previousState = state
        update()

        return previousState == State.AFTER_INTERPOL
    }

    /**
     * *Does not* implicitly update the state after being called.
     *
     * @return whether the LexState was in a string interpolation.
     */
    fun isAfterInterpol() = state == State.AFTER_INTERPOL

    /**
     * Increments `this` [depth].
     *
     * @throws InterpolTooDeepException if [depth] exceeds [MAX_INTERPOL_DEPTH].
     */
    fun deepenInterpol() {
        if (depth >= MAX_INTERPOL_DEPTH) {
            throw InterpolTooDeepException()
        }

        depth++
    }

    /**
     * Sets `this` [State] to [State.AFTER_INTERPOL], and decrements the depth.
     */
    fun endInterpol() {
        state = State.AFTER_INTERPOL
        depth--
    }

    /**
     * Sets this [State] to [State.NORMAL].
     */
    private fun update() {
        if (state != State.AFTER_INTERPOL) {
            return
        }

        state = State.NORMAL
    }
}