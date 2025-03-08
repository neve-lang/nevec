package type.rec

import type.Type

class Fields(vararg pairs: Pair<String, Type>) {
    private val fields = mapOf(*pairs)
}