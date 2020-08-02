package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.testContainer
import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.test.captureOutput
import io.briones.gradle.render.ErrorRenderer
import io.briones.gradle.render.SummarizingRenderer
import io.briones.gradle.render.defaultUnicodeSymbols
import org.junit.jupiter.api.Test

class SummarizingRendererTest {
    @Test
    fun `it renders correctly when all tests pass`() {
        val captured = captureOutput {
            val out = IndentingOutputWriter(it, indent = " ")
            val errorRenderer = SummarizingRenderer(defaultUnicodeSymbols)
            testContainer(out, errorRenderer) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                }
            }
        }
        assertThat(captured).isEqualTo("""
        
        
        ✓ 3 passing (150ms)
        
        """.trimIndent())
    }

    @Test
    fun `it renders correctly if some tests skipped`() {
        val captured = captureOutput {
            val out = IndentingOutputWriter(it, indent = " ")
            val errorRenderer = SummarizingRenderer(defaultUnicodeSymbols)
            testContainer(out, errorRenderer) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testSkipped("Test Two")
                    testPassed("Test Three")
                    testPassed("Test Four")
                }
            }
        }
        assertThat(captured).isEqualTo("""
        
        
        ✓ 3 passing (200ms)
          1 skipped
        
        """.trimIndent())
    }

    @Test
    fun `it renders correctly if some tests failed`() {
        val captured = captureOutput {
            val out = IndentingOutputWriter(it, indent = " ")
            val errorRenderer = SummarizingRenderer(defaultUnicodeSymbols)
            testContainer(out, errorRenderer) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testSkipped("Test Two")
                    testPassed("Test Three")
                    testFailed("Test Four", RuntimeException("boom"))
                }
            }
        }
        assertThat(captured).isEqualTo("""
        
        
        2 passing (200ms)
        1 failing
        1 skipped
        
        """.trimIndent())
    }
}