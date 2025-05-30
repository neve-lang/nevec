package feature.impl.file

import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

/**
 * Simple type alias that represents a **file ID**—the number that appears before the `.neve` extension in test files
 * found in `neve-test/`.
 */
typealias FileId = Int

/**
 * @return A new [FileId] from a file’s full [Path].
 */
fun FileIdCompanion.from(path: Path): FileId {
    return path.nameWithoutExtension.toInt()
}

private typealias FileIdCompanion = Int.Companion
