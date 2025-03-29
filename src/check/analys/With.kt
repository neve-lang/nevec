package check.analys

import ast.typeful.FirstType
import type.chance.Chances

class With(val typeful: FirstType) {
    // making this inline probably isn't a pretty solution, but hey, we need to live with the JVM's type erasure
    inline fun <reified T : Chances> of(transform: (T) -> Chances): Chances? {
        val chances = typeful.firstType().chances

        if (chances !is T) {
            return null
        }

        return transform(chances)
    }
}