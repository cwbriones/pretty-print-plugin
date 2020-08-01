package io.briones.gradle.format

import io.briones.gradle.output.IndentingOutputWriter

enum class Format {
    Dot,
    List,
    Mocha;

    internal fun listener(): TestReporter<IndentingOutputWriter> = when (this) {
        Dot -> newDotPrintingReporter(80)
        List -> newListPrintingReporter()
        Mocha -> TreePrintingReporter()
    }

    fun supportsInlineExceptions(): Boolean = when (this) {
        List -> true
        Mocha -> true
        else -> false
    }
}
