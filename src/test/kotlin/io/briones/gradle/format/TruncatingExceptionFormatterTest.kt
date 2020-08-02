package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class TruncatingExceptionFormatterTest {
    @Test
    fun `configured with both stacktrace and cause`() {
        val e = testException()
        val formatter = TruncatingExceptionFormatter(showCauses = true, showStackTraces = true)
        assertThat(formatter.format(e)).isEqualTo(
            """
            |java.lang.RuntimeException: boom
            |    at io.example.Example.itBlowsUp(Example.java:42)
            |    at io.example.ExampleTest.testItBlowsUp(ExampleTest.java:57)
            |Caused by: java.lang.RuntimeException: i'm the cause
            |    at io.example.Example.throwAfter(Example.java:51)
            |    at io.example.Example.throwAfter(Example.java:48)
            |    at io.example.Example.itBlowsUp(Example.java:40)
            """.trimMargin()
        )
    }

    @Test
    fun `configured with only stacktrace`() {
        val e = testException()
        val formatter = TruncatingExceptionFormatter(showCauses = false, showStackTraces = true)
        assertThat(formatter.format(e)).isEqualTo(
            """
            |java.lang.RuntimeException: boom
            |    at io.example.Example.itBlowsUp(Example.java:42)
            |    at io.example.ExampleTest.testItBlowsUp(ExampleTest.java:57)
            """.trimMargin()
        )
    }

    @Test
    fun `configured with only cause`() {
        val e = testException()
        val formatter = TruncatingExceptionFormatter(showCauses = true, showStackTraces = false)
        assertThat(formatter.format(e)).isEqualTo(
            """
            |java.lang.RuntimeException: boom
            |Caused by: java.lang.RuntimeException: i'm the cause
            """.trimMargin()
        )
    }

    @Test
    fun `configured with neither stacktrace nor cause`() {
        val e = testException()
        val formatter = TruncatingExceptionFormatter(showCauses = false, showStackTraces = false)
        assertThat(formatter.format(e)).isEqualTo(
            """
            |java.lang.RuntimeException: boom
            """.trimMargin()
        )
    }

    @Test
    fun `it truncates the stack trace`() {
        val e = testExceptionWithinRunner()
        val formatter = TruncatingExceptionFormatter(showCauses = true, showStackTraces = true)
        val formatted = formatter.format(e, javaClass.name)
        val expectedPatterns = listOf(
            "java.lang.RuntimeException: boom",
            "    at ${javaClass.name}\\..*\\(${javaClass.simpleName}\\.kt:\\d+\\)",
            "    at ${javaClass.name}\\..*\\(${javaClass.simpleName}\\.kt:\\d+\\)"
        )
        assertThat(formatted.lines().count()).isEqualTo(expectedPatterns.size)
        for ((pat, line) in expectedPatterns.zip(formatted.lines())) {
            assertThat(line).matches(pat)
        }
    }

    private fun testException(): Throwable {
        val cause = RuntimeException("i'm the cause")
        cause.stackTrace = arrayOf(
            StackTraceElement("io.example.Example", "throwAfter", "Example.java", 51),
            StackTraceElement("io.example.Example", "throwAfter", "Example.java", 48),
            StackTraceElement("io.example.Example", "itBlowsUp", "Example.java", 40)
        )
        val e = RuntimeException("boom", cause)
        e.stackTrace = arrayOf(
            StackTraceElement("io.example.Example", "itBlowsUp", "Example.java", 42),
            StackTraceElement("io.example.ExampleTest", "testItBlowsUp", "ExampleTest.java", 57)
        )
        return e
    }

    private fun testExceptionWithinRunner(): Throwable {
        return RuntimeException("boom")
    }
}
