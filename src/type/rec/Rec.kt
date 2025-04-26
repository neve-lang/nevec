package type.rec

import file.module.Module
import type.NamedType
import type.kind.TypeKind
import type.WrappedType
import type.gen.param.TypeParams
import type.rec.field.Fields

data class Rec(
    val module: Module, val name: String, val fields: Fields, val params: TypeParams
) : WrappedType, NamedType {
    companion object {
        fun builder(): RecBuilder {
            return RecBuilder()
        }
    }

    override fun wrap(): TypeKind {
        return TypeKind.OfRec(this)
    }

    override fun named(): String {
        return name
    }
}