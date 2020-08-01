package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.testContainer
import io.briones.gradle.output.test.captureOutput
import org.junit.jupiter.api.Test

class ListPrintingReporterTest {
    @Test
    fun `it renders correctly when all tests passed`() {
        val captured = captureOutput {
            val dotReporter = newListPrintingReporter()
            testContainer(it, dotReporter) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                }
            }
        }
        assertThat("""
        ✓ Top level suite > Test One (50ms)
        ✓ Top level suite > Test Two (50ms)
        ✓ Top level suite > Test Three (50ms)
        
        """.trimIndent()).isEqualTo(captured)
    }

    @Test
    fun `it renders correctly some tests failed`() {
        val captured = captureOutput {
            val dotReporter = newListPrintingReporter()
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
        ✓ Top level suite > Test One (50ms)
        - Top level suite > Test Two (50ms)
        ✓ Top level suite > Test Three (50ms)
        ✗ Top level suite > Test Four (50ms)
        
        """.trimIndent()).isEqualTo(captured)
    }
}