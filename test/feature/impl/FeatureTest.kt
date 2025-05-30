package feature.impl

import feature.impl.file.FileId
import feature.impl.file.from
import feature.impl.outcome.Outcome
import feature.impl.result.ExpectMap
import feature.impl.result.TestExpect
import feature.impl.result.TestResult
import feature.impl.routine.TestRoutine
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.*

/**
 * Takes care of feature tests by testing the entire test folder.  The folder is located using the name of the feature.
 *
 * @param name The name of the feature to be tested.
 * @param expects The [TestExpects][TestExpect] given by the user, **mapped by folder name**.
 */
class FeatureTest(
    private val name: String,
    private val expects: ExpectMap
) {
    init {
        require(expects.folders().all { isExpectedFolder(it) }) {
            """
            The given `ExpectMap` must only contain **expected** folder names.
            
            The expected names include:
            - parse
            """.trimIndent()
        }
    }

    companion object {
        /**
         * Maps test folder names to [TestRoutines][TestRoutine].
         */
        private val TEST_ROUTINES = mapOf(
            "parse" to TestRoutine.forParseTests()
        )
    }

    fun check(): Boolean {
        val folders = try {
            open()
        } catch (e: IOException) {
            println("could not open test folder ${name}.")
            fail()
            emptyList()
        }

        return folders.map {
            expects[it.name]!! to testFolder(it)
        }.map {
            (expected, actual) -> expected.compare(with = actual)
        }.all {
            summary -> !summary.hasDiscrepancies().also { println(summary.msg()) }
        }
    }

    private fun testFolder(folder: Path): TestResult {
        println(" → Testing `${folder.name}` folder:")

        return folder.listDirectoryEntries().sortedBy {
            FileId.from(it)
        }.map {
            test(it, from = folder)
        }.let(TestResult::from)
    }

    private fun test(file: Path, from: Path): Pair<FileId, Outcome> {
        return TEST_ROUTINES[from.name]!!.let {
            routine -> FileId.from(file) to routine.run(file)
        }
    }

    private fun open(): List<Path> {
        val folder = path()

        if (!folder.isDirectory()) {
            throw IOException()
        }

        return folder
            .listDirectoryEntries()
            .filter { isExpectedFolder(it.name) }
            .map(Path::toRealPath)
    }

    private fun path(): Path {
        return Path("neve-test/feature/${name}/")
    }

    private fun isExpectedFolder(name: String): Boolean {
        return name in TEST_ROUTINES.keys
    }

    private fun fail() {
        assert(false)
    }
}