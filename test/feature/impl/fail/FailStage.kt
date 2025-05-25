package feature.impl.fail

/**
 * Represents a stage at which executing a test can fail.
 *
 * Keep in mind that an **execution failure** and a **test failure** are not the same—a test failure implies that
 * the implementation has some error, whereas an execution failure may be expected by the test itself—especially
 * when it comes to type checker tests.
 *
 * These stages include:
 *
 * - At the CLI stage: [CLI].
 * - At the structural phase: [STRUCTURAL].
 * - At the compile-time phase: [COMPILE].
 * - At the runtime phase: [RUNTIME].
 */
enum class FailStage {
    /**
     * Represents an execution failure that occurred at the CLI stage: i.e. the user gave improper input to the `nevec`
     * command.
     */
    CLI,

    /**
     * Represents an execution failure that occurred when the compiler was still building the dependency graph.
     *
     * Such a failure usually indicates that the program was malformed.
     */
    STRUCTURAL,

    /**
     * Represents an execution failure that occurs at compile time.
     */
    COMPILE,

    /**
     * Represents an execution failure that occurs at runtime: i.e. an assertion error.
     */
    RUNTIME
}