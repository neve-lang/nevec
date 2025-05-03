package util.extension

/**
 * @return Whether `this` [isWhitespace] *and* is not a newline (`\n`) character.
 */
fun Char.isInsignificant(): Boolean {
    return this != '\n' && this.isWhitespace()
}
