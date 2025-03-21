package parse.state

class ParseState {
    var hadErr = false
    var isPanicking = false

    fun relax() {
        isPanicking = false
    }

    fun markErr() {
        hadErr = true
        panic()
    }

    private fun panic() {
        isPanicking = true
    }
}