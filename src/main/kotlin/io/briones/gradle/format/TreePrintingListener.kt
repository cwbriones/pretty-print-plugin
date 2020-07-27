package io.briones.gradle.format

import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.bold
import io.briones.gradle.output.failure
import io.briones.gradle.output.plain
import io.briones.gradle.output.success
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

class TreePrintingListener(
    out: OutputWriter,
    displayFailuresInline: Boolean
) : BasePrintingListener(out, displayFailuresInline) {

    override fun beforeSuiteRun(suite: TestDescriptor) {
        out.plain().println(suite.displayName)
        out.increaseIndentation()
    }

    @ExperimentalStdlibApi
    override fun afterSuiteRun(suite: TestDescriptor, result: TestResult) {
        out.decreaseIndentation()
    }

    override fun afterTestRun(testDescriptor: TestDescriptor, result: TestResult) {
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
}
