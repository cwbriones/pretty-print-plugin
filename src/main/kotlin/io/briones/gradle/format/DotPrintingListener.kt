package io.briones.gradle.format

import io.briones.gradle.output.GradleOutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.internal.logging.text.StyledTextOutput

class DotPrintingListener(out: StyledTextOutput) : TestListener {
    // Keep track of the index of each node so we know when we can print.
    private var output = GradleOutputWriter(out)

    override fun beforeSuite(suite: TestDescriptor?) {}

    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        if (result == null) {
            return
        }
        if (suite?.parent != null) {
            return
        }
        val elapsed = result.humanReadableDuration()
        output
            .println()
            .success()
            .append("  ${result.successfulTestCount} passing")
            .normal()
            .println(" ($elapsed)")
            .applyingIf(result.failedTestCount > 0) {
                it.failure().println("  ${result.failedTestCount} failing")
            }
            .applyingIf(result.skippedTestCount > 0) {
                it.info().println("  ${result.skippedTestCount} skipped")
            }
    }

    override fun beforeTest(testDescriptor: TestDescriptor?) {}

    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> output.normal().append(".").normal().flush()
            TestResult.ResultType.FAILURE -> output.failure().append("X").flush()
            TestResult.ResultType.SKIPPED -> output.info().append("s").flush()
            else -> output.normal().flush()
        }
    }
}

