package check.analys.help

import ast.typeful.Typeful
import chance.Chances

class With(val typeful: Typeful) {
    // making this inline probably isn't a pretty solution, but hey, we need to live with the JVM's type erasure
    inline fun <reified T : Chances> of(transform: (T) -> Chances): Chances? {
        val chances = typeful.patientType().chances

        if (chances !is T) {
            return null
        }

        return transform(chances)
    }
}