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
import java.time.Duration
import javax.inject.Inject

class PrettyPrintTestPlugin @Inject constructor(
    private val outputFactory: StyledTextOutputFactory
) : Plugin<Project> {
    override fun apply(project: Project) {
        val out = outputFactory.create(javaClass)
        val standardEvents = arrayOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
        project.tasks.named<Test>("test") {
            testLogging {
                events(*standardEvents)
                exceptionFormat = TestExceptionFormat.FULL
                showExceptions = true
                showCauses = true
                showStackTraces = true

                info {
                    events(
                        *standardEvents,
                        TestLogEvent.STANDARD_OUT
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                }

                debug {
                    events(
                        *standardEvents,
                        TestLogEvent.STANDARD_OUT,
                        TestLogEvent.STANDARD_ERROR
                    )
                    exceptionFormat = TestExceptionFormat.FULL
                }

                afterSuite { suite, result ->
                    if (suite.parent != null) {
                        return@afterSuite
                    }
                    val elapsed = durationToHumanString(Duration.ofMillis(result.endTime - result.startTime))
                    val resultSummary =
                        listOf("${result.resultType} (${result.testCount} tests",
                               "${result.successfulTestCount} passed",
                               "${result.failedTestCount} failed",
                               "${result.skippedTestCount} skipped)").joinToString(", ")
                    val summary = joinInBox(
                        "${project.name}$path $resultSummary in $elapsed",
                        "",
                        "Report: ${reports.html.entryPoint}"
                    )
                    val style = when (result.resultType) {
                        TestResult.ResultType.FAILURE -> StyledTextOutput.Style.Failure
                        else -> StyledTextOutput.Style.Success
                    }
                    out.style(style).println(summary)
                }
            }
        }
    }

    /** Convenience extension for specifying an after-suite callback. */
    private fun Test.afterSuite(action: (TestDescriptor, TestResult) -> Unit) {
        addTestListener(object : TestListener {
            override fun beforeTest(testDescriptor: TestDescriptor?) {}
            override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
                if (suite == null || result == null) {
                    return
                }
                action(suite, result)
            }
            override fun beforeSuite(suite: TestDescriptor?) {}
            override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
            }
        })
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
            return "0.${duration.toMillisPart()}s"
        }
        val seconds = duration.toSecondsPart()
        val minutes = duration.minusSeconds(seconds.toLong()).toMinutes()
        val display = mutableListOf<String>()
        if (minutes > 0) {
            display.add("${minutes}m")
        }
        display.add("${seconds}s")
        return display.joinToString(" ")
    }
}