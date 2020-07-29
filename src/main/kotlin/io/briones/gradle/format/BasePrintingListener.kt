package io.briones.gradle.format

import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.failure
import io.briones.gradle.output.info
import io.briones.gradle.output.plain
import io.briones.gradle.output.success
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

class TestFailure(
    val descriptor: TestDescriptor,
    val exception: Throwable
)

abstract class BasePrintingListener(
    out: OutputWriter,
    private val displayFailuresInline: Boolean
) : TestListener {
    protected var out = IndentingOutputWriter(out, indent = "  ", base = 1)
    private var failures = mutableListOf<TestFailure>()

    override fun beforeTest(testDescriptor: TestDescriptor?) { /* unused */ }

    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        afterTestRun(testDescriptor, result)
        if (result.resultType == TestResult.ResultType.FAILURE) {
            val e = result.exception ?: throw IllegalStateException("Failed test must have exception")
            if (displayFailuresInline) {
                val trace = formattedStackTrace(e, testDescriptor.className)
                out.failure().indented { println(trace) }
                return
            }
            failures.add(TestFailure(testDescriptor, e))
        }
    }

    override fun beforeSuite(suite: TestDescriptor?) {
        if (suite != null && suite.parent == null) {
            out.println()
        }
        if (suite?.className != null) {
            beforeSuiteRun(suite)
        }
    }

    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        if (result == null || suite == null) {
            return
        }
        if (suite.className != null) {
            afterSuiteRun(suite, result)
        }
        if (suite.parent == null) {
            displayFailures()
            summarize(result)
        }
    }

    /** Non-null version of [TestListener.afterTest] */
    abstract fun afterTestRun(testDescriptor: TestDescriptor, result: TestResult)

    /** Non-null version of [TestListener.beforeSuite] */
    open fun beforeSuiteRun(suite: TestDescriptor) { /* unused */ }

    /** Non-null version of [TestListener.afterSuite] */
    open fun afterSuiteRun(suite: TestDescriptor, result: TestResult) { /* unused */ }

    private fun summarize(result: TestResult) {
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
            .applyingIf(allPassed) {
                it.append("✓ ")
            }
            .append("$successCount passing")
            .plain()
            .println(" ($elapsed)")
            .applyingIf(!allPassed) {
                it.failure().println("$failedCount failing")
            }
            .applyingIf(result.skippedTestCount > 0) {
                it.info().println("$skippedCount skipped")
            }
    }

    private fun displayFailures() {
        if (failures.isEmpty()) {
            return
        }
        out.println()
        for ((i, failure) in failures.withIndex()) {
            val e = failure.exception
            val fullName = failure.descriptor.fqDisplayName()
            val trace = formattedStackTrace(e, failure.descriptor.className)
            out.failure()
                .println()
                .println("${i + 1}) $fullName")
                .println()
                .indented { println(trace) }
        }
    }
}
