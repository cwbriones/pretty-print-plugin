package io.briones.gradle.format

import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

class ListPrintingListener(private var out: OutputWriter) : TestListener {
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
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> out.success().append("✓").plain()
            TestResult.ResultType.FAILURE -> out.failure().append("✗")
            TestResult.ResultType.SKIPPED -> out.plain().append("-")
            else -> out.plain().append(" ")
        }
        val elapsed = result.humanReadableDuration()
        val displayName = buildName(testDescriptor)
        out.println(" $displayName ($elapsed)")
        result.exception?.let {
            val trace = formattedStackTrace(it, testDescriptor.className)
            out.failure().println(trace)
        }
    }

    private fun buildName(descriptor: TestDescriptor): String {
        return generateSequence(descriptor, { it.parent })
            .filter { it.className != null }
            .map { it.displayName }
            .toList()
            .reversed()
            .joinToString(separator = " > ")
    }
}

