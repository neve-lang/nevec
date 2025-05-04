package meta

import meta.asserts.MetaAssert

/**
 * Encapsulates both [meta assertions][MetaAssert] and `meta annotations` into a same data class.
 */
data class Meta(val asserts: List<MetaAssert<*>>)