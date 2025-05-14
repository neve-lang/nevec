package meta.target

/**
 * Represents a target attached to some **meta component**, i.e. a [meta assertion][meta.asserts.MetaAssert] or
 * a meta annotation.
 *
 * A target usually maps to a type of node in the AST or parse rule—for example, the [PRIMARY] target only
 * applies to primary expressions.
 */
enum class Target {
    /**
     * Denotes a meta component that only applied to **primary expressions**.
     *
     * This includes parenthesized expressions.
     */
    PRIMARY;

    /**
     * @return `this` [Target]’s name, in lowercase.
     */
    fun lowercase(): String {
        return name.lowercase()
    }
}