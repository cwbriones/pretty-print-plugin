package io.briones.gradle.render

import io.briones.gradle.format.humanReadableDuration
import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.failure
import io.briones.gradle.output.plain
import io.briones.gradle.output.success
import org.gradle.api.tasks.testing.TestResult

fun newListPrintingRenderer(symbols: Symbols): TestRenderer<OutputWriter> = SimpleTestRenderer { out, testDescriptor, result ->
    when (result.resultType) {
        TestResult.ResultType.SUCCESS -> out.success().append(symbols.success).plain()
        TestResult.ResultType.FAILURE -> out.failure().append(symbols.failure)
        TestResult.ResultType.SKIPPED -> out.plain().append(symbols.skipped)
        else -> out.plain().append(" ")
    }
    val elapsed = humanReadableDuration(result.getDuration())
    val displayName = testDescriptor.fqDisplayName()
    out.println(" $displayName ($elapsed)")
}
