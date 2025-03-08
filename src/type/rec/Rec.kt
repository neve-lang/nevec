package type.rec

import file.module.Module
import type.Type
import type.WrappedType
import type.gen.TypeParams

data class Rec(val module: Module, val name: String, val fields: Fields, val params: TypeParams) : WrappedType {
    companion object {
        private fun builder() = RecBuilder()

        fun prelude(name: String) = builder().from(Module.prelude()).named(name)
    }

    override fun wrap(): Type {
        return Type.RecType(this)
    }
}