package io.briones.gradle.format

import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.OutputWriter
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

    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        if (result == null) {
            return
        }
        if (suite?.parent != null) {
            return
        }
        displayFailures()
        summarize(result)
    }

    override fun beforeSuite(testDescriptor: TestDescriptor?) { /* unused */ }

    private fun summarize(result: TestResult) {
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
            out.indented { println(trace) }
        }
    }

    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        afterTestRun(testDescriptor, result)
        if (result.resultType == TestResult.ResultType.FAILURE) {
            val e = result.exception ?: throw IllegalStateException("Failed test must have exception")
            if (displayFailuresInline) {
                val trace = formattedStackTrace(e, testDescriptor.className)
                out.failure()
                out.indented { println(trace) }
                return
            }
            failures.add(TestFailure(testDescriptor, e))
        }
    }

    abstract fun afterTestRun(testDescriptor: TestDescriptor, result: TestResult)
}

