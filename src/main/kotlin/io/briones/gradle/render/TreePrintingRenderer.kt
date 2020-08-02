package io.briones.gradle.render

import io.briones.gradle.format.humanReadableDuration
import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.bold
import io.briones.gradle.output.failure
import io.briones.gradle.output.plain
import io.briones.gradle.output.success
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

class TreePrintingRenderer : TestRenderer<IndentingOutputWriter> {
    override fun renderTestResult(out: IndentingOutputWriter, testDescriptor: TestDescriptor, result: TestResult) {
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> out.success().append("✓").plain()
            TestResult.ResultType.FAILURE -> out.failure().append("✗")
            TestResult.ResultType.SKIPPED -> out.plain().append("-")
            else -> out.plain().append(" ")
        }
        val elapsed = result.humanReadableDuration()
        out.bold()
            .append(" ${testDescriptor.displayName}")
            .plain()
            .println(" ($elapsed)")
    }

    override fun renderSuite(out: IndentingOutputWriter, suiteDescriptor: TestDescriptor) {
        // Ideally we could say at a higher level if this is due to the test
        // runner (gradle) or if it's actually a test class
        if (suiteDescriptor.className == null) {
            return
        }
        out.plain().println(suiteDescriptor.displayName)
        out.increaseIndentation()
    }

    override fun renderSuiteResult(out: IndentingOutputWriter, suiteDescriptor: TestDescriptor, result: TestResult) {
        if (suiteDescriptor.className == null) {
            return
        }
        out.decreaseIndentation()
    }
}
