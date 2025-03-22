package type.rec.field

import type.Type

class Fields(vararg fields: Field) {
    private val fields = listOf(*fields)
}