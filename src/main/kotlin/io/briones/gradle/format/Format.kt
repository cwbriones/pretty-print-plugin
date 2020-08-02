package io.briones.gradle.format

import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.render.Symbols
import io.briones.gradle.render.TestRenderer
import io.briones.gradle.render.TreePrintingRenderer
import io.briones.gradle.render.newDotPrintingRenderer
import io.briones.gradle.render.newListPrintingRenderer

enum class Format {
    Dot,
    List,
    Mocha;

    internal fun listener(symbols: Symbols): TestRenderer<IndentingOutputWriter> = when (this) {
        Dot -> newDotPrintingRenderer(80)
        List -> newListPrintingRenderer(symbols)
        Mocha -> TreePrintingRenderer(symbols)
    }

    internal fun reporters(symbols: Symbols): TestRenderer<IndentingOutputWriter> = when (this) {
        Dot -> newDotPrintingRenderer(80)
        List -> newListPrintingRenderer(symbols)
        Mocha -> TreePrintingRenderer(symbols)
    }

    fun supportsInlineExceptions(): Boolean = when (this) {
        List -> true
        Mocha -> true
        else -> false
    }
}
