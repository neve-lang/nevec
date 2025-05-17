package meta.comp

import meta.comp.asserts.MetaAssert
import meta.input.ReqsInput
import meta.target.AppliesTo

/**
 * Semi-marker interface that includes [AppliesTo] for **meta components**.
 *
 * @see MetaAssert
 */
interface MetaComp : AppliesTo, ReqsInput