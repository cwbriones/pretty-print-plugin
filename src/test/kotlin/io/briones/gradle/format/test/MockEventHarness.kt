package io.briones.gradle.format.test

import io.briones.gradle.render.TestRenderer
import io.briones.gradle.output.OutputWriter
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import java.time.Instant

class MockClock(start: Instant = Instant.now()) {
    var instant: Instant = start
        private set

    val now: Long
        get() = instant.toEpochMilli()

    fun tickMs(ms: Long) {
        instant = instant.plusMillis(ms)
    }
}

class MockSuiteContainer(
    private val suiteDescriptor: TestDescriptor,
    private val listener: TestListener,
    private val clock: MockClock
) {
    private val testRuns: MutableList<TestResult> = mutableListOf()

    fun suite(name: String, displayName: String = name, events: MockSuiteContainer.() -> Unit) {
        val descriptor = MockTestDescriptor(
            suiteDescriptor,
            true,
            "MockSuite",
            name,
            displayName
        )
        listener.beforeSuite(descriptor)
        val container = MockSuiteContainer(descriptor, listener, clock)
        val start = clock.now
        events(container)
        val containerResult = container.result(start, clock.now)
        listener.afterSuite(descriptor, containerResult)
        testRuns.add(containerResult)
    }

    fun testFailed(
        name: String,
        exception: Throwable,
        displayName: String = name,
        elapsedMs: Long = 50
    ) {
        val descriptor = testDescriptor(name, displayName)
        val start = clock.now
        clock.tickMs(elapsedMs)
        listener.beforeTest(descriptor)
        val result = FailedTestResult(start, clock.now, exception)
        testRuns.add(result)
        listener.afterTest(descriptor, result)
    }

    fun testPassed(
        name: String,
        displayName: String = name,
        elapsedMs: Long = 50
    ) {
        val descriptor = testDescriptor(name, displayName)
        val start = clock.now
        clock.tickMs(elapsedMs)
        listener.beforeTest(descriptor)
        val result = SuccessfulTestResult(start, clock.now)
        testRuns.add(result)
        listener.afterTest(descriptor, result)
    }

    fun testSkipped(
        name: String,
        displayName: String = name,
        elapsedMs: Long = 50
    ) {
        val descriptor = testDescriptor(name, displayName)
        val start = clock.now
        clock.tickMs(elapsedMs)
        listener.beforeTest(descriptor)
        val result = SkippedTestResult(start, clock.now)
        testRuns.add(result)
        listener.afterTest(descriptor, result)
    }

    fun result(start: Long, end: Long): TestResult = CompositeTestResult(testRuns, start, end)

    private fun testDescriptor(name: String, displayName: String): TestDescriptor = MockTestDescriptor(
        suiteDescriptor,
        false,
        "MockTest",
        name,
        displayName
    )
}

class CompositeTestResult(
    private val results: List<TestResult>,
    private val propStartTime: Long,
    private val propEndTime: Long
) : TestResult {
    override fun getSuccessfulTestCount(): Long {
        return results.map { it.successfulTestCount }.sum()
    }

    override fun getSkippedTestCount(): Long {
        return results.map { it.skippedTestCount }.sum()
    }

    override fun getResultType(): TestResult.ResultType {
        return if (failedTestCount > 0) {
            TestResult.ResultType.FAILURE
        } else {
            TestResult.ResultType.SUCCESS
        }
    }

    override fun getFailedTestCount(): Long {
        return results.map { it.failedTestCount }.sum()
    }

    override fun getException(): Throwable? = null

    override fun getTestCount(): Long = results.map { it.testCount }.sum()

    override fun getExceptions(): List<Throwable> {
        return results.mapNotNull { it.exception }
            .toList()
    }

    override fun getStartTime() = propStartTime
    override fun getEndTime() = propEndTime
}

class MockTestDescriptor(
    private val propParent: TestDescriptor?,
    private val propComposite: Boolean,
    private val propClassName: String?,
    private val propName: String,
    private val propDisplayName: String? = null
) : TestDescriptor {
    override fun getParent(): TestDescriptor? = propParent
    override fun isComposite(): Boolean = propComposite
    override fun getName(): String = propName
    override fun getDisplayName(): String = propDisplayName ?: propName
    override fun getClassName(): String? = propClassName
}

abstract class MockLeafTestResult(
    private val propStartTime: Long,
    private val propEndTime: Long
) : TestResult {
    override fun getEndTime(): Long = propEndTime
    override fun getStartTime(): Long = propStartTime
    override fun getException(): Throwable? = null
    override fun getExceptions(): List<Throwable> = listOf()
    override fun getTestCount(): Long = successfulTestCount + skippedTestCount + failedTestCount
    override fun getSuccessfulTestCount(): Long = 0
    override fun getSkippedTestCount(): Long = 0
    override fun getFailedTestCount(): Long  = 0
}

class SuccessfulTestResult(
    propStartTime: Long,
    propEndTime: Long
) : MockLeafTestResult(propStartTime, propEndTime) {
    override fun getSuccessfulTestCount(): Long = 1
    override fun getResultType(): TestResult.ResultType = TestResult.ResultType.SUCCESS
}

class SkippedTestResult(
    propStartTime: Long,
    propEndTime: Long
) : MockLeafTestResult(propStartTime, propEndTime) {
    override fun getSkippedTestCount(): Long = 1
    override fun getResultType(): TestResult.ResultType = TestResult.ResultType.SKIPPED
}

class FailedTestResult(
    propStartTime: Long,
    propEndTime: Long,
    private val propException: Throwable
) : MockLeafTestResult(propStartTime, propEndTime) {
    override fun getFailedTestCount(): Long = 1
    override fun getResultType(): TestResult.ResultType = TestResult.ResultType.FAILURE
    override fun getException(): Throwable = propException
    override fun getExceptions(): List<Throwable> = listOf(propException)
}

fun <T: OutputWriter> testContainer(out: T, renderer: TestRenderer<T>, events: MockSuiteContainer.() -> Unit) {
    val listener = renderer.toListener(out)
    testContainer(listener, events)
}

// FIXME: This should not be duplicated within MockSuiteContainer
fun testContainer(listener: TestListener, events: MockSuiteContainer.() -> Unit) {
    val topLevelDescriptor = MockTestDescriptor(
        null,
        true,
        null,
        "Mock Test Container",
        "Mock Test Container"
    )
    val clock = MockClock(Instant.parse("2020-07-31T12:34:56Z"))
    val start = clock.now
    val container = MockSuiteContainer(
        topLevelDescriptor,
        listener,
        clock
    )
    listener.beforeSuite(topLevelDescriptor)
    events(container)
    listener.afterSuite(topLevelDescriptor, container.result(start, clock.now))
}
