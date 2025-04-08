package check.analys.diagnose

import chance.Chances
import check.analys.implies.Implies

data class Diagnose(val chances: Chances?, val implies: Implies?) {
    companion object {
        fun from(chances: Chances?) = Diagnose(chances, null)
    }
}

fun Chances.diagnosed() = Diagnose.from(this)

fun Chances?.diagnosed() = Diagnose.from(this)
