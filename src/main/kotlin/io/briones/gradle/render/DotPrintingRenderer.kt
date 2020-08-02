package io.briones.gradle.render

import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.bold
import io.briones.gradle.output.failure
import io.briones.gradle.output.info
import io.briones.gradle.output.plain
import io.briones.gradle.render.SimpleTestRenderer
import io.briones.gradle.render.TestRenderer
import org.gradle.api.tasks.testing.TestResult

fun newDotPrintingRenderer(maxWidth: Int): TestRenderer<OutputWriter> {
    var lineWidth = 0
    return SimpleTestRenderer { out, _, result ->
        if (lineWidth == maxWidth) {
            out.println()
            lineWidth = 0
        }
        lineWidth++
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> out.bold().append(".").plain().flush()
            TestResult.ResultType.FAILURE -> out.failure().append("X").flush()
            TestResult.ResultType.SKIPPED -> out.info().append("s").flush()
            else -> out.plain().flush()
        }
    }
}
