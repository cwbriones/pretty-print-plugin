package io.briones.gradle.format

import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

class TreePrintingListener(out: OutputWriter) : TestListener {
    // Keep track of the index of each node so we know when we can print.
    private var testNodes = mutableListOf<Int>()
    private var output = IndentingOutputWriter(
        out,
        indent = "  ",
        base = 1
    )

    override fun beforeSuite(suite: TestDescriptor?) {
        if (suite?.className == null) {
            return
        }
        if (testNodes.isEmpty()) {
            output.println()
        }
        output.plain().println(suite.displayName)
        testNodes.add(0)
        output.indentLevel = testNodes.size
    }

    @ExperimentalStdlibApi
    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        if (suite?.className != null) {
            testNodes.removeLast()
            output.indentLevel = testNodes.size
        }
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

    override fun beforeTest(testDescriptor: TestDescriptor?) {
        testNodes[testNodes.lastIndex]++
    }

    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> output.success().append("✓").plain()
            TestResult.ResultType.FAILURE -> output.failure().append("✗")
            TestResult.ResultType.SKIPPED -> output.plain().append("-")
            else -> output.plain().append(" ")
        }
        val elapsed = result.humanReadableDuration()
        output.bold()
            .append(" ${testDescriptor.displayName}")
            .plain()
            .println(" ($elapsed)")
        result.exception?.let {
            val trace = formattedStackTrace(it, testDescriptor.className)
            output.failure().println(trace)
        }
    }
}

