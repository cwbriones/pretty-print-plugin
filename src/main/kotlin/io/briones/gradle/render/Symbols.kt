package io.briones.gradle.render

interface Symbols {
    val success: String
    val failure: String
    val skipped: String
}

class FixedSymbols(
    override val success: String,
    override val failure: String,
    override val skipped: String
) : Symbols

val defaultUnicodeSymbols: Symbols = FixedSymbols(
    success = "✓",
    failure = "✗",
    skipped = "·"
)

class FailureCountingSymbols(
    private val symbols: Symbols
) : Symbols by symbols {
    private var count = 0
    override val failure: String
        get() {
            count += 1
            return "$count)"
        }
}
