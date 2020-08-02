package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.testContainer
import io.briones.gradle.output.test.captureOutput
import io.briones.gradle.render.newDotPrintingRenderer
import org.junit.jupiter.api.Test

class DotPrintingReporterTest {
    @Test
    fun `it renders correctly when all tests passed`() {
        val captured = captureOutput {
            val dotReporter = newDotPrintingRenderer(80)
            testContainer(it, dotReporter) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                }
            }
        }
        assertThat(captured).isEqualTo("""
        ...
        """.trimIndent())
    }

    @Test
    fun `it renders correctly some tests failed`() {
        val captured = captureOutput {
            val dotReporter = newDotPrintingRenderer(80)
            testContainer(it, dotReporter) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testSkipped("Test Two")
                    testPassed("Test Three")
                    testFailed("Test Four", RuntimeException("boom"))
                }
            }
        }
        assertThat("""
        .s.X
        """.trimIndent()).isEqualTo(captured)
    }
}