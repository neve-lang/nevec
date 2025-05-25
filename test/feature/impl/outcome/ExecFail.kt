package feature.impl.outcome

/**
 * Represents a kind of execution failure.
 *
 * Keep in mind that an **execution failure** and a **test failure** are not the same—a test failure implies that
 * the implementation has some error, whereas an execution failure may be expected by the test itself—especially
 * when it comes to type checker tests.
 *
 * The kinds include:
 *
 * - At the CLI stage: [CLI].
 * - At the structural phase: [STRUCTURAL].
 * - At the compile-time phase: [COMPILE].
 * - A VM segmentation fault: [VM_SEGFAULT].
 * - A VM memory leak fault: [VM_LEAK].
 * - A native binary segmentation fault: [NATIVE_SEGFAULT].
 * - A native binary memory leak fault: [NATIVE_LEAK].
 * - An assertion error: [ASSERT].
 * - An error that occurs because `valgrind` was not installed: [MISSING_VALGRIND].
 * - A discrepancy between VM behavior and native behavior: [IRREGULAR_BEHAVIOR].
 */
enum class ExecFail {
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
     * Represents an execution failure that was caused by a segfault in the virtual machine.
     *
     * This usually indicates a serious bug.
     */
    VM_SEGFAULT,

    /**
     * Represents an execution failure that was caused by a memory leak during VM execution.
     *
     * It is checked using `valgrind`.  If `valgrind` is not installed, [MISSING_VALGRIND] will be returned.
     *
     * I’m a bit of a maniac when it comes to this stuff, so right now, errors are checked with the following flags:
     *
     * ```
     * --leak-check=full --show-leak-kinds=all --errors-for-leak-kinds=all --error-exitcode=1
     * ```
     *
     * However, if `--error-for-leak-kinds=all` seems too impractical, I’m open to changing the policy.
     */
    VM_LEAK,

    /**
     * Represents an execution failure that was caused by a segmentation fault in the native binary.
     *
     * This usually indicates a serious compiler bug.
     */
    NATIVE_SEGFAULT,

    /**
     * Represents an execution failure that was caused by a memory leak during native execution.
     *
     * It is checked using `valgrind`.  If `valgrind` is not installed, [MISSING_VALGRIND] will be returned.
     *
     * I’m a bit of a maniac when it comes to this stuff, so right now, errors are checked with the following flags:
     *
     * ```
     * --leak-check=full --show-leak-kinds=all --errors-for-leak-kinds=all --error-exitcode=1
     * ```
     *
     * However, if `--error-for-leak-kinds=all` seems too impractical, I’m open to changing the policy.
     */
    NATIVE_LEAK,

    /**
     * Represents an execution failure that was caused by an assertion error.
     */
    ASSERT,

    /**
     * Represents an execution failure that was caused by a missing `valgrind`.
     */
    MISSING_VALGRIND,

    /**
     * Represents an error that was caused by a discrepancy between VM behavior and native behavior.
     */
    IRREGULAR_BEHAVIOR
}