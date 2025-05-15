package feature

import hook.TestHook
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.*

/**
 * Takes care of feature tests by testing the entire test folder.  The folder is located using the name of the feature.
 *
 * @param name The name of the feature to be tested.
 */
class FeatureTest(private val name: String) {
    companion object {
        private val TEST_FOLDERS = listOf(
            "parse"
        )
    }

    fun succeeds(): Boolean {
        val folders = try {
            open()
        } catch (e: IOException) {
            println("could not open test folder ${name}.")
            fail()
            emptyList()
        }

        return folders.flatMap(::testFolder).all { it }
    }

    private fun testFolder(folder: Path): List<Boolean> {
        return folder.listDirectoryEntries().sortedBy {
            it.nameWithoutExtension.toInt()
        }.map {
            test(it)
        }
    }

    private fun test(file: Path): Boolean {
        return TestHook.okay(argsFor(file))
    }

    private fun open(): List<Path> {
        val folder = path()

        if (!folder.isDirectory()) {
            throw IOException()
        }

        return folder
            .listDirectoryEntries()
            .filter { it.name in TEST_FOLDERS }
            .map(Path::toRealPath)
    }

    private fun path(): Path {
        return Path("neve-test/feature/${name}/")
    }

    private fun argsFor(filename: Path): Array<String> {
        return arrayOf(
            filename.toString(),
            "--meta-asserts",
            "--compiler-types",
            "--no-opt"
        )
    }

    private fun fail() {
        assert(false)
    }
}