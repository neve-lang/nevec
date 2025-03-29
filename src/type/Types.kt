package type

object Types {
    private val types = mutableMapOf<String, Type>()

    fun register(type: Type) {
        require(!type.isPoisoned() && !type.isFree()) { "Cannot register free or poisoned types" }

        types[type.fullName()] = type
    }
}
