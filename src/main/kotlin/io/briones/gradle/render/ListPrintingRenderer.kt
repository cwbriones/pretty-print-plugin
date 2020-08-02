package io.briones.gradle.render

import io.briones.gradle.format.fqDisplayName
import io.briones.gradle.format.humanReadableDuration
import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.failure
import io.briones.gradle.output.plain
import io.briones.gradle.output.success
import org.gradle.api.tasks.testing.TestResult

fun newListPrintingRenderer(): TestRenderer<OutputWriter> = SimpleTestRenderer { out, testDescriptor, result ->
    when (result.resultType) {
        TestResult.ResultType.SUCCESS -> out.success().append("✓").plain()
        TestResult.ResultType.FAILURE -> out.failure().append("✗")
        TestResult.ResultType.SKIPPED -> out.plain().append("-")
        else -> out.plain().append(" ")
    }
    val elapsed = result.humanReadableDuration()
    val displayName = testDescriptor.fqDisplayName()
    out.println(" $displayName ($elapsed)")
}
