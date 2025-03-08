package type.gen

class TypeParams(val params: List<TypeParam>) {
    companion object {
        fun none() = TypeParams(listOf())

        fun from(vararg names: String): TypeParams {
            val list = names.mapIndexed { i, name -> TypeParam(name, i) }
            return TypeParams(list)
        }
    }
}