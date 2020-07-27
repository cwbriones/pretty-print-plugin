package io.briones.gradle.format

import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

class DotPrintingListener(
    out: OutputWriter
) : BasePrintingListener(out, false) {
    private var lineWidth = 0

    override fun afterTestRun(testDescriptor: TestDescriptor, result: TestResult) {
        if (lineWidth == MAX_WIDTH) {
            out.println()
            lineWidth = 0
        }
        lineWidth++
        when (result.resultType) {
            TestResult.ResultType.SUCCESS -> out.bold().append(".").plain().flush()
            TestResult.ResultType.FAILURE -> out.failure().append("X").flush()
            TestResult.ResultType.SKIPPED -> out.info().append("s").flush()
            else -> out.plain().flush()
        }
    }

    companion object {
        private const val MAX_WIDTH = 80
    }
}

