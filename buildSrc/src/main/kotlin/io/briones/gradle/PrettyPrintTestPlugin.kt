package io.briones.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.named
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Duration
import javax.inject.Inject

class PrettyPrintTestPlugin @Inject constructor(
    private val outputFactory: StyledTextOutputFactory
) : Plugin<Project> {

    override fun apply(project: Project) {
        val out = outputFactory.create(javaClass)
        project.tasks.named<Test>("test") {
            testLogging {
                setEvents(listOf<TestLogEvent>())

                exceptionFormat = TestExceptionFormat.FULL
                showExceptions = false
                showStackTraces = false
                showCauses = false

                info {
                    events(
                        TestLogEvent.STANDARD_OUT,
                        TestLogEvent.STANDARD_ERROR
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                }

                debug {
                    events(
                        TestLogEvent.STANDARD_OUT,
                        TestLogEvent.STANDARD_ERROR
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                }

                afterSuite(out) { suite, result ->
                    if (suite.parent != null) {
                        return@afterSuite
                    }
                    val elapsed = durationToHumanString(Duration.ofMillis(result.endTime - result.startTime))
                    out.style(StyledTextOutput.Style.Success)
                        .append("  ${result.successfulTestCount} passing")
                        .withStyle(StyledTextOutput.Style.Normal)
                        .println(" ($elapsed)")
                    if (result.failedTestCount > 0) {
                        out.withStyle(StyledTextOutput.Style.Failure)
                            .println("  ${result.failedTestCount} failing")
                    }
                    if (result.skippedTestCount > 0) {
                        out.withStyle(StyledTextOutput.Style.Info)
                            .println("  ${result.skippedTestCount} skipped")
                    }
                }
            }
        }
    }

    /** Convenience extension for specifying an after-suite callback. */
    private fun Test.afterSuite(out: StyledTextOutput, action: (TestDescriptor, TestResult) -> Unit) {
        addTestListener(object : TestListener {
            override fun beforeTest(testDescriptor: TestDescriptor?) {}

            override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
                if (suite == null || result == null) {
                    return
                }
                action(suite, result)
            }

            override fun beforeSuite(suite: TestDescriptor?) {
                val indent =
                    generateSequence(suite?.parent, { it.parent })
                        .map { "  " }
                        .drop(1)
                        .joinToString(separator = "")
                println("$indent${suite?.displayName}")
            }

            override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
                if (testDescriptor == null || result == null) {
                    return
                }
                val indent =
                    generateSequence(testDescriptor.parent, { it.parent })
                        .map { "  " }
                        .drop(1)
                        .joinToString(separator = "")
                val (sym, style) = when (result.resultType) {
                    TestResult.ResultType.SUCCESS -> Pair("✓", StyledTextOutput.Style.Success)
                    TestResult.ResultType.FAILURE -> Pair("✗", StyledTextOutput.Style.Failure)
                    TestResult.ResultType.SKIPPED -> Pair("-", StyledTextOutput.Style.Normal)
                    else -> Pair(" ", StyledTextOutput.Style.Normal)
                }
                val elapsed = durationToHumanString(Duration.ofMillis(result.endTime - result.startTime))
                out.append(indent)
                    .style(style)
                    .append(sym)
                    .style(StyledTextOutput.Style.Normal)
                    .println(" ${testDescriptor.displayName} ($elapsed)")
                result.exception?.let {
                    formattedStackTrace(it, testDescriptor.className)
                        .lines()
                        .forEach { line ->
                            out.style(StyledTextOutput.Style.Failure)
                                .append(indent)
                                .println(line)
                        }
                }
            }
        })
    }

    private fun formattedStackTrace(e: Throwable, className: String?): String {
        truncateStackTrace(e, className)
        e.cause?.let { truncateStackTrace(it) }
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    private fun truncateStackTrace(e: Throwable, className: String? = null) {
        val end = sequenceOf(*e.stackTrace)
            .takeWhile { s -> !s.isNativeMethod }
            .takeWhile { s -> className == null || s.className == className }
            .count()

        e.stackTrace = e.stackTrace.copyOfRange(0, end)
    }

    /** Return a string that contains the given lines surrounded by a box. */
    private fun joinInBox(vararg lines: String ): String {
        val lineLength = lines.map { it.length }.max() !!

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
        val decimalSeconds = duration.toMillisPart() / 100;
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
