package io.briones.gradle.format

import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.applyIf
import io.briones.gradle.output.failure
import io.briones.gradle.output.info
import io.briones.gradle.output.plain
import io.briones.gradle.output.success
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

class SummarizingReporter: TestReporter<IndentingOutputWriter> {
    override fun afterSuite(out: IndentingOutputWriter, suiteDescriptor: TestDescriptor, result: TestResult) {
        if (suiteDescriptor.parent == null) {
            summarize(out, result)
        }
    }

    private fun summarize(out: IndentingOutputWriter, result: TestResult) {
        val elapsed = result.humanReadableDuration()
        val allPassed = result.failedTestCount == 0L
        val padding = listOf(
            result.successfulTestCount,
            result.failedTestCount,
            result.skippedTestCount
        ).max().toString().length
        // account for the leading checkmark.
        val skipPadding = if (allPassed) padding + 2 else padding

        val successCount = result.successfulTestCount.toString().padStart(padding)
        val skippedCount = result.skippedTestCount.toString().padStart(skipPadding)
        val failedCount = result.failedTestCount.toString().padStart(padding)

        out
            .println()
            .println()
            .success()
            .applyIf(allPassed) {
                it.append("✓ ")
            }
            .append("$successCount passing")
            .plain()
            .println(" ($elapsed)")
            .applyIf(!allPassed) {
                it.failure().println("$failedCount failing")
            }
            .applyIf(result.skippedTestCount > 0) {
                it.info().println("$skippedCount skipped")
            }
    }
}