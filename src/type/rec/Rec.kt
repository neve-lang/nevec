package type.rec

import type.Type
import type.WrappedType
import type.gen.TypeParams

data class Rec(val module: String, val name: String, val fields: Fields, val params: TypeParams) : WrappedType {
    companion object {
        private fun builder() = RecBuilder()

        fun prelude(name: String) = builder().from("prelude").named(name)
    }

    override fun wrap(): Type {
        return Type.RecType(this)
    }
}