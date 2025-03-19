package parse.state

class ParseState {
    var hadErr = false
    var isPanicking = false

    fun relax() {
        isPanicking = false
    }

    fun panic() {
        isPanicking = true
    }

    fun hadErr() {
        hadErr = true
        panic()
    }
}