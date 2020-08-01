package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.MockTestDescriptor
import org.junit.jupiter.api.Test
import io.briones.gradle.format.test.SuccessfulTestResult
import org.gradle.api.tasks.testing.TestDescriptor
import org.junit.jupiter.api.Nested

class FormattingTests {
    @Nested
    inner class DurationPrettyPrinting {
        @Test
        fun `exactly one second`() {
            val result = SuccessfulTestResult(0, 1000)
            assertThat(result.humanReadableDuration()).isEqualTo("1.0s")
        }

        @Test
        fun `a second and a little bit`() {
            val result = SuccessfulTestResult(0, 1007)
            assertThat(result.humanReadableDuration()).isEqualTo("1.0s")
        }

        @Test
        fun `a second and a little more`() {
            val result = SuccessfulTestResult(0, 1300)
            assertThat(result.humanReadableDuration()).isEqualTo("1.3s")
        }

        @Test
        fun `less than one second`() {
            val result = SuccessfulTestResult(0, 420)
            assertThat(result.humanReadableDuration()).isEqualTo("420ms")
        }

        @Test
        fun `greater than one minute`() {
            val result = SuccessfulTestResult(0, 63_000)
            assertThat(result.humanReadableDuration()).isEqualTo("1m 3s")
        }

        @Test
        fun `a ridiculously long test`() {
            val result = SuccessfulTestResult(0, 7_326_000)
            assertThat(result.humanReadableDuration()).isEqualTo("2h 2m 6s")
        }
    }

    @Nested
    inner class FullyQualifiedDisplayName {
        @Test
        fun `when there's no parent`() {
            val test = MockTestDescriptor(null, false, "", "My Test")
            assertThat(test.fqDisplayName()).isEqualTo(test.displayName)
        }

        @Test
        fun `when theres several layers of nesting`() {
            var prev: TestDescriptor? = null
            for (i in 1..4) {
                prev = MockTestDescriptor(prev, true, "", "Suite #$i")
            }
            val test = MockTestDescriptor(prev, false, "", "Testcase")
            assertThat(test.fqDisplayName()).isEqualTo("Suite #1 > Suite #2 > Suite #3 > Suite #4 > Testcase")
        }

        @Test
        fun `it respects the separator argument`() {
            var prev: TestDescriptor? = null
            for (i in 1..4) {
                prev = MockTestDescriptor(prev, true, "", "Suite #$i")
            }
            val test = MockTestDescriptor(prev, false, "", "Testcase")
            assertThat(test.fqDisplayName("|")).isEqualTo("Suite #1|Suite #2|Suite #3|Suite #4|Testcase")
        }
    }
}