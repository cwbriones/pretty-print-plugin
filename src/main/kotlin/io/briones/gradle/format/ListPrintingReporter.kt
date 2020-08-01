package io.briones.gradle.format

import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.failure
import io.briones.gradle.output.plain
import io.briones.gradle.output.success
import org.gradle.api.tasks.testing.TestResult

fun newListPrintingReporter(): TestReporter<OutputWriter> = SimpleTestReporter { out, testDescriptor, result ->
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
