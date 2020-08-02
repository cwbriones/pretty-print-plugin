package io.briones.gradle.render

import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.failure
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import java.io.PrintWriter
import java.io.StringWriter

/**
 * A reporter that displays exceptions from failed tests.
 *
 * If `displayFailuresInline` is true, the exceptions will be reported after each individual
 * test run. They will otherwise be reported after all tests have completed.
 */
class ErrorRenderer(
    private val showInline: Boolean,
    private val showStackTraces: Boolean,
    private val showCauses: Boolean
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
        if (showInline) {
            val trace = formattedStackTrace(e, showStackTraces, showCauses, testDescriptor.className)
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
            val trace = formattedStackTrace(e, showStackTraces, showCauses, failure.descriptor.className)
            out.failure()
                .println()
                .println("${i + 1}) $fullName")
                .println()
                .indented {
                    println(trace)
                }
        }
    }
}

fun formattedStackTrace(e: Throwable,
                        showStackTraces: Boolean,
                        showCauses: Boolean,
                        testClassName: String?): String {
    truncateStackTrace(e, showStackTraces, null)
    val cause = e.cause
    if (cause != null) {
        truncateStackTrace(cause, showStackTraces, testClassName)
    }
    val sw = StringWriter()
    e.printStackTrace(PrintWriter(sw))
    if (showCauses)
        return sw.toString().trim()
    return sw.toString()
        .lines()
        .takeWhile { !it.startsWith("Caused by:") }
        .joinToString(separator="\n")
}

private fun truncateStackTrace(e: Throwable, showStackTraces: Boolean, testClassName: String?) {
    if (!showStackTraces) {
        e.stackTrace = arrayOf()
    }
    val end = e.stackTrace
        .takeWhile {
            !it.isNativeMethod
                && (testClassName == null
                || it.className != testClassName)
        }
        .count()
    e.stackTrace = e.stackTrace.copyOfRange(0, end)
}
