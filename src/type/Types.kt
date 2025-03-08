package type

object Types {
    private val types = mutableMapOf<String, Type>()

    fun register(type: Type) {
        require(!type.isPoisoned()) { "Cannot register poisoned types" }

        types[type.fullName()] = type
    }
}

// using '!!' here because Types does not allow registering poisoned types
fun Type.fullName() = moduleName()!! + "." + name()!!

fun Type.moduleName(): String? = when (this) {
    is Type.PrimType -> prim.type.moduleName()
    is Type.RecType -> rec.module.name
    is Type.RefineType -> type.moduleName()
    is Type.PoisonedType -> null
}

fun Type.name(): String? = when (this) {
    is Type.PrimType -> prim.type.name()
    is Type.RecType -> rec.name
    is Type.RefineType -> type.name()
    is Type.PoisonedType -> null
}
