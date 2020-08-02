package io.briones.gradle.render

import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

/**
 * `TestRenderer` is a similar abstraction to Gradle's `TestListener` but with the following
 * differences:
 *
 * * All of the test reporters arguments are non-null.
 * * A test renderer is generic over a particular type of output writer.
 *
 * The latter point in particular means that we can abstract over output modes allowing for the renderer
 * to state the capabilities it requires to function.
 */
interface TestRenderer<in T: OutputWriter> {
    fun renderTestResult(out: T, testDescriptor: TestDescriptor, result: TestResult) {}

    fun renderSuite(out: T, suiteDescriptor: TestDescriptor) {}

    fun renderSuiteResult(out: T, suiteDescriptor: TestDescriptor, result: TestResult) {}

    // Factory to create a listener given an OutputWriter.
    fun toListener(out: T): TestListener = object : TestListener {
        override fun beforeTest(testDescriptor: TestDescriptor?) {}

        override fun afterSuite(suiteDescriptor: TestDescriptor?, suiteResult: TestResult?) {
            if (suiteDescriptor == null || suiteResult == null) {
                return
            }
            renderSuiteResult(out, suiteDescriptor, suiteResult)
        }

        override fun beforeSuite(suiteDescriptor: TestDescriptor?) {
            if (suiteDescriptor == null) {
                return
            }
            renderSuite(out, suiteDescriptor)
        }

        override fun afterTest(testDescriptor: TestDescriptor?, testResult: TestResult?) {
            if (testDescriptor == null || testResult == null) {
                return
            }
            renderTestResult(out, testDescriptor, testResult)
        }
    }
}

/**
 * Helper for implementing a test reporter when you only care about `renderTest` events.
 */
class SimpleTestRenderer<in T: OutputWriter>(renderTest: (T, TestDescriptor, TestResult) -> Unit) : TestRenderer<T> {
    private val renderTestProp = renderTest

    override fun renderTestResult(out: T, testDescriptor: TestDescriptor, result: TestResult) {
        renderTestProp(out, testDescriptor, result)
    }

    override fun renderSuite(out: T, suiteDescriptor: TestDescriptor) {}

    override fun renderSuiteResult(out: T, suiteDescriptor: TestDescriptor, result: TestResult) {}
}
