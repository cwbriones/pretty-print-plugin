package io.briones.gradle.format

import io.briones.gradle.PrettyPrintTestExtension
import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestListener

enum class Format {
    Dot,
    List,
    Mocha;

    internal fun listener(out: OutputWriter, ext: PrettyPrintTestExtension): TestListener = when (this) {
        Dot -> DotPrintingListener(out)
        List -> ListPrintingListener(out, ext.inlineExceptions)
        Mocha -> TreePrintingListener(out)
    }
}
