package io.briones.gradle.format

import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

/**
 * TestReporter is a similar abstraction to Gradle's `TestListener` but with the following
 * differences:
 *
 * * All of the test reporters arguments are non-null.
 * * A test reporter is generic over a particular type of output writer.
 *
 * The latter point in particular means that we can abstract over output modes allowing for the reporter
 * to state the capabilities it requires to function.
 */
interface TestReporter<in T: OutputWriter> {
    fun afterTest(out: T, testDescriptor: TestDescriptor, result: TestResult) {}

    fun beforeSuite(out: T, suiteDescriptor: TestDescriptor) {}

    fun afterSuite(out: T, suiteDescriptor: TestDescriptor, result: TestResult) {}

    // Factory to create a listener given an OutputWriter.
    fun toListener(out: T): TestListener = object : TestListener {
        override fun beforeTest(testDescriptor: TestDescriptor?) {}

        override fun afterSuite(suiteDescriptor: TestDescriptor?, suiteResult: TestResult?) {
            if (suiteDescriptor == null || suiteResult == null) {
                return
            }
            afterSuite(out, suiteDescriptor, suiteResult)
        }

        override fun beforeSuite(suiteDescriptor: TestDescriptor?) {
            if (suiteDescriptor == null) {
                return
            }
            beforeSuite(out, suiteDescriptor)
        }

        override fun afterTest(testDescriptor: TestDescriptor?, testResult: TestResult?) {
            if (testDescriptor == null || testResult == null) {
                return
            }
            afterTest(out, testDescriptor, testResult)
        }
    }
}

/**
 * Helper for implementing a test reporter when you only care about `afterTest` events.
 */
class SimpleTestReporter<in T: OutputWriter>(afterTest: (T, TestDescriptor, TestResult) -> Unit) : TestReporter<T> {
    private val afterTestProp = afterTest

    override fun afterTest(out: T, testDescriptor: TestDescriptor, result: TestResult) {
        afterTestProp(out, testDescriptor, result)
    }

    override fun beforeSuite(out: T, suiteDescriptor: TestDescriptor) {}

    override fun afterSuite(out: T, suiteDescriptor: TestDescriptor, result: TestResult) {}
}
