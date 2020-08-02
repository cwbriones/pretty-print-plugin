package io.briones.gradle

import io.briones.gradle.output.OutputWriter
import io.briones.gradle.render.TestRenderer
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

/**
 * Entry point into test reporting.
 *
 * This Listener filters out any null events or results before directing them
 * to the underlying reporters.
 */
class PrettyPrintListener<T : OutputWriter>(
    private val out: T,
    private val renderers: List<TestRenderer<T>>
) : TestListener {
    override fun beforeTest(testDescriptor: TestDescriptor?) { /* unused */ }

    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        renderers.forEach {
            it.renderTestResult(out, testDescriptor, result)
        }
    }

    override fun beforeSuite(suite: TestDescriptor?) {
        if (suite != null && suite.parent == null) {
            out.println()
        }
        if (suite != null) {
            renderers.forEach {
                it.renderSuite(out, suite)
            }
        }
    }

    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        if (result == null || suite == null) {
            return
        }
        renderers.forEach {
            it.renderSuiteResult(out, suite, result)
        }
    }
}
