package io.briones.gradle.format

import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

/**
 * Entry point into test reporting.
 *
 * This Listener filters out any null events or results before directing them
 * to the underlying reporters.
 */
class PrettyPrintingListener<T: OutputWriter>(
    private val out: T,
    private val reporters: List<TestReporter<T>>
) : TestListener {
    override fun beforeTest(testDescriptor: TestDescriptor?) { /* unused */ }

    override fun afterTest(testDescriptor: TestDescriptor?, result: TestResult?) {
        if (testDescriptor == null || result == null) {
            return
        }
        reporters.forEach {
            it.afterTest(out, testDescriptor, result)
        }
    }

    override fun beforeSuite(suite: TestDescriptor?) {
        if (suite != null && suite.parent == null) {
            out.println()
        }
        if (suite != null) {
            reporters.forEach {
                it.beforeSuite(out, suite)
            }
        }
    }

    override fun afterSuite(suite: TestDescriptor?, result: TestResult?) {
        if (result == null || suite == null) {
            return
        }
        reporters.forEach {
            it.afterSuite(out, suite, result)
        }
    }
}

