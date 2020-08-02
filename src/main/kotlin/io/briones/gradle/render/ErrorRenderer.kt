package io.briones.gradle.render

import io.briones.gradle.format.formattedStackTrace
import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.failure
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

/**
 * A reporter that displays exceptions from failed tests.
 *
 * If `displayFailuresInline` is true, the exceptions will be reported after each individual
 * test run. They will otherwise be reported after all tests have completed.
 */
class ErrorRenderer(
    private val displayFailuresInline: Boolean
) : TestRenderer<IndentingOutputWriter> {
    private class TestFailure(
        val descriptor: TestDescriptor,
        val exception: Throwable
    )

    private var failures = mutableListOf<TestFailure>()

    override fun renderTestResult(out: IndentingOutputWriter, testDescriptor: TestDescriptor, result: TestResult) {
        if (result.resultType != TestResult.ResultType.FAILURE) {
            return
        }
        val e = result.exception ?: throw IllegalStateException("Failed test must have exception")
        if (displayFailuresInline) {
            val trace = formattedStackTrace(e, testDescriptor.className)
            out.failure().indented { println(trace) }
            return
        }
        failures.add(TestFailure(testDescriptor, e))
    }

    override fun renderSuiteResult(out: IndentingOutputWriter, suiteDescriptor: TestDescriptor, result: TestResult) {
        if (suiteDescriptor.parent != null) {
            return
        }
        displayFailures(out)
    }

    private fun displayFailures(out: IndentingOutputWriter) {
        if (failures.isEmpty()) {
            return
        }
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
