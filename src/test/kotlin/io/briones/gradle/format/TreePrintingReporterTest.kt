package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.testContainer
import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.test.captureOutput
import io.briones.gradle.render.TreePrintingRenderer
import io.briones.gradle.render.defaultUnicodeSymbols
import org.junit.jupiter.api.Test

class TreePrintingReporterTest {
    @Test
    fun `test all passed`() {
        val captured = captureOutput {
            val reporter = TreePrintingRenderer(defaultUnicodeSymbols)
            val out = IndentingOutputWriter(it, indent="  ")
            testContainer(out, reporter) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                    suite("Nested") {
                        testPassed("Nested One")
                        testPassed("Nested Two")
                    }
                    testPassed("Test Four")
                    testPassed("Test Five")
                    testPassed("Test Six")
                }
                suite("Top level suite 2") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                }
            }
        }
        assertThat(captured).isEqualTo("""
        Top level suite
          ✓ Test One (50ms)
          ✓ Test Two (50ms)
          ✓ Test Three (50ms)
          Nested
            ✓ Nested One (50ms)
            ✓ Nested Two (50ms)
          ✓ Test Four (50ms)
          ✓ Test Five (50ms)
          ✓ Test Six (50ms)
        Top level suite 2
          ✓ Test One (50ms)
          ✓ Test Two (50ms)
          ✓ Test Three (50ms)
        
        """.trimIndent())
    }

    @Test
    fun `test reporting failure`() {
        val captured = captureOutput {
            val reporter = TreePrintingRenderer(defaultUnicodeSymbols)
            val out = IndentingOutputWriter(it, indent="  ")
            testContainer(out, reporter) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testFailed("Failing Test", dummyThrowable("boom", 3))
                    testPassed("Test Three")
                }
                suite("Top level suite 2") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                }
            }
        }
        assertThat(captured).isEqualTo("""
        Top level suite
          ✓ Test One (50ms)
          ✓ Test Two (50ms)
          ✗ Failing Test (50ms)
          ✓ Test Three (50ms)
        Top level suite 2
          ✓ Test One (50ms)
          ✓ Test Two (50ms)
          ✓ Test Three (50ms)
        
        """.trimIndent())
    }
}

