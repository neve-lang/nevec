package type.rec

import type.Type
import type.gen.TypeParams

class RecBuilder {
    private var module: String? = null
    private var name: String? = null
    private var fields: Fields? = null
    private var params: TypeParams? = null

    fun from(module: String) = apply { this.module = module }

    fun named(name: String) = apply { this.name = name }

    fun fields(vararg pairs: Pair<String, Type>) = apply { this.fields = Fields(*pairs) }

    fun params(params: TypeParams) = apply { this.params = params }

    fun build(): Rec {
        require(module != null) { "A module must be provided" }
        require(name != null) { "A name must be provided" }

        return Rec(module!!, name!!, fields ?: Fields(), params ?: TypeParams.none())
    }
}