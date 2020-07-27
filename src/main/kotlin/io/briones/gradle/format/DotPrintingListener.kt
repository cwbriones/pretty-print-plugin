package io.briones.gradle.format

import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

class DotPrintingListener(out: OutputWriter) : TestListener {
    private var out = IndentingOutputWriter(out, indent = "  ", base = 1)
    private var lineWidth = 0

    override fun beforeSuite(suite: TestDescriptor?) {}

    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        if (result == null) {
            return
        }
        if (suite?.parent != null) {
            return
        }
        val elapsed = result.humanReadableDuration()
        out
            .println()
            .println()
            .success()
            .append("${result.successfulTestCount} passing")
            .plain()
            .println(" ($elapsed)")
            .applyingIf(result.failedTestCount > 0) {
                it.failure().println("${result.failedTestCount} failing")
            }
            .applyingIf(result.skippedTestCount > 0) {
                it.info().println("${result.skippedTestCount} skipped")
            }
    }

    override fun beforeTest(testDescriptor: TestDescriptor?) {}

    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        if (lineWidth == MAX_WIDTH) {
            out.println()
            lineWidth = 0
        }
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> out.bold().append(".").plain().flush()
            TestResult.ResultType.FAILURE -> out.failure().append("X").flush()
            TestResult.ResultType.SKIPPED -> out.info().append("s").flush()
            else -> out.plain().flush()
        }
        lineWidth++
    }

    companion object {
        private const val MAX_WIDTH = 80
    }
}

