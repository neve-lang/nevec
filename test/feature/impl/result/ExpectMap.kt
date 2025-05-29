package feature.impl.result

/**
 * Represents a map of [TestExpect] that maps each **test folder** to a specific [TestExpect].
 *
 * @property folderToExpect Maps each folder to a specific [TestExpect].  The string keys represent the **name** as a
 * **relative path** of the folder—that is, the `parse/` folder should be stored as `"parse"`.
 */
data class ExpectMap(
    private val folderToExpect: Map<String, TestExpect> = mapOf()
) {
    companion object {
        /**
         * @param expect The [TestExpect] in question.
         *
         * @return A new [ExpectMap] that only applies the given [expect] to the `parse/` folder.
         */
        fun forParseTests(expect: TestExpect): ExpectMap {
            return ExpectMap(
                mapOf("parse" to expect)
            )
        }
    }
}