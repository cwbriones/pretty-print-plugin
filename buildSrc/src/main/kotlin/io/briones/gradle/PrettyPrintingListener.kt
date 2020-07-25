package io.briones.gradle

import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.internal.logging.text.StyledTextOutput
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Duration

class PrettyPrintingListener(out: StyledTextOutput) : TestListener {
    // Keep track of the index of each node so we know when we can print.
    private var testNodes = mutableListOf<Int>()
    private var output = IndentingOutputWriter(
        GradleOutputWriter(out),
        indent = "  ",
        base = 1
    )

    override fun beforeTest(testDescriptor: TestDescriptor?) {
        testNodes[testNodes.lastIndex]++
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
        val elapsed = durationToHumanString(Duration.ofMillis(result.endTime - result.startTime))

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

    @ExperimentalStdlibApi
    override fun beforeSuite(suite: TestDescriptor?) {
        if (suite?.className == null) {
            return
        }
        if (testNodes.isEmpty()) {
            output.println()
        }
        output.normal().println("${suite.name}")
        testNodes.add(0)
        output.indentLevel = testNodes.size
    }

    @ExperimentalStdlibApi
    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> output.success().append("✓").normal()
            TestResult.ResultType.FAILURE -> output.failure().append("✗")
            TestResult.ResultType.SKIPPED -> output.normal().append("-")
            else -> output.normal().append(" ")
        }
        val elapsed = durationToHumanString(Duration.ofMillis(result.endTime - result.startTime))
        output.println(" ${testDescriptor.displayName} ($elapsed)")
        result.exception?.let {
            formattedStackTrace(it, testDescriptor.className)
                .lines()
                .forEach { line ->
                    output.failure().println(line)
                }
        }
    }

    private fun formattedStackTrace(e: Throwable, className: String?): String {
        truncateStackTrace(e, className)
        e.cause?.let { truncateStackTrace(it) }
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString().trim()
    }

    private fun truncateStackTrace(e: Throwable, className: String? = null) {
        val end = sequenceOf(*e.stackTrace)
            .takeWhile { s -> !s.isNativeMethod }
            .takeWhile { s -> className == null || s.className == className }
            .count()

        e.stackTrace = e.stackTrace.copyOfRange(0, end)
    }

    /** Return a string that contains the given lines surrounded by a box. */
    private fun joinInBox(vararg lines: String): String {
        val lineLength = lines.map { it.length }.max()!!

        val bordered = mutableListOf<String>()
        bordered.add("┌${"─".repeat(lineLength + 2)}┐")
        lines.forEach {
            val padded = it.padEnd(lineLength, ' ')
            bordered.add("│ $padded │")
        }
        bordered.add("└${"─".repeat(lineLength + 2)}┘")
        return bordered.joinToString("\n", postfix = "\n")
    }

    /** Return the duration as a more human-readable string. e.g 120s => 2m */
    private fun durationToHumanString(duration: Duration): String {
        if (duration < Duration.ofSeconds(1)) {
            return "${duration.toMillisPart()}ms"
        }
        val decimalSeconds = duration.toMillisPart() / 100
        val seconds = duration.toSecondsPart()
        val minutes = duration.minusSeconds(seconds.toLong()).toMinutes()
        val display = mutableListOf<String>()
        if (minutes > 0) {
            display.add("${minutes}m")
        }
        display.add("${seconds}.${decimalSeconds}s")
        return display.joinToString(" ")
    }
}

