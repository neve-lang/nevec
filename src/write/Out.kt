package write

import ctx.Ctx
import nevec.result.Aftermath
import nevec.result.Fail
import stage.Stage
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.exists

/**
 * Outputs the final OCaml-transpiled code for a given module.
 *
 * It does so by creating a `build-{project_name}` folder and creating the OCaml module files there.  It also
 * creates a `.nevecache` file that stores timestamps of the last time a module was compiled, as a baby incremental
 * compilation optimization.
 *
 * At the time of writing, `Out` only creates a build directory named `build` in the CWD, and if one
 * already exists, it simply increments it with an index: `build-1`, `build-2`, etc.
 *
 * There is no baby incremental compilation system as of now.
 */
class Out : Stage<String, Unit> {
    companion object {
        const val DEFAULT_BUILD_FOLDER_NAME = "build"
    }


    override fun perform(data: String, ctx: Ctx): Aftermath<Unit> {
        val path = Path.of("${buildFolderPath()}/out.ml")
        val file = path.toFile()

        return try {
            Files.createDirectories(path.parent)

            (if (file.exists())
                file
            else
                Files.createFile(path).toFile()).let {
                it.bufferedWriter().use {
                    out -> out.write(data)
                }
            }.let {
                Aftermath.Success(Unit)
            }
        } catch (e: FileSystemException) {
            Aftermath.OfFail(Fail.IO)
        }
    }

    @SuppressWarnings
    private fun buildFolderPath(name: String = DEFAULT_BUILD_FOLDER_NAME): Path {
        return Path.of(shortenNameIfPossible(name))

        // this will be useful later, but for now, we leave it unused.
        return if (Path.of(name).exists(LinkOption.NOFOLLOW_LINKS))
            buildFolderPath(differentiatedName(name))
        else
            Path.of(shortenNameIfPossible(name))
    }

    private fun differentiableName(name: String): String {
        return if (name.last().isDigit())
            name
        else
            "$name-0"
    }

    private fun shortenNameIfPossible(name: String): String {
        return if (name.endsWith("-0"))
            name.dropLast(2)
        else
            name
    }

    private fun differentiatedName(name: String): String {
        val differentiable = differentiableName(name)

        return if (differentiable.endsWith("0"))
            "build-1"
        else
            "build-" + differentiable.last().digitToInt().inc()
    }
}