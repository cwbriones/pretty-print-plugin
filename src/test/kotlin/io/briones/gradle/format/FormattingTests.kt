package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.MockTestDescriptor
import io.briones.gradle.render.fqDisplayName
import org.gradle.api.tasks.testing.TestDescriptor
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Duration

class FormattingTests {
    @Nested
    inner class DurationPrettyPrinting {
        @Test
        fun `exactly one second`() {
            val result = humanReadableDuration(Duration.ofSeconds(1))
            assertThat(result).isEqualTo("1.0s")
        }

        @Test
        fun `a second and a little bit`() {
            val result = humanReadableDuration(Duration.ofMillis(1007))
            assertThat(result).isEqualTo("1.0s")
        }

        @Test
        fun `a second and a little more`() {
            val result = humanReadableDuration(Duration.ofMillis(1301))
            assertThat(result).isEqualTo("1.3s")
        }

        @Test
        fun `less than one second`() {
            val result = humanReadableDuration(Duration.ofMillis(420))
            assertThat(result).isEqualTo("420ms")
        }

        @Test
        fun `greater than one minute`() {
            val result = humanReadableDuration(Duration.ofSeconds(63))
            assertThat(result).isEqualTo("1m 3s")
        }

        @Test
        fun `a ridiculously long test`() {
            val fullDuration =
                Duration.ofHours(2)
                    .plus(Duration.ofMinutes(2))
                    .plus(Duration.ofSeconds(6))
            val result = humanReadableDuration(fullDuration)
            assertThat(result).isEqualTo("2h 2m 6s")
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
