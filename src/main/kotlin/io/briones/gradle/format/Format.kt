package io.briones.gradle.format

import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestListener

enum class Format {
    Dot,
    List,
    Mocha;

    internal fun listener(out: OutputWriter): TestListener = when (this) {
        Dot -> DotPrintingListener(out)
        List -> ListPrintingListener(out)
        Mocha -> TreePrintingListener(out)
    }
}