package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.testContainer
import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.test.captureOutput
import io.briones.gradle.render.ErrorRenderer
import org.junit.jupiter.api.Test

class ErrorRendererTest {
    @Test
    fun `it renders nothing when all tests pass`() {
        val captured = captureOutput {
            val out = IndentingOutputWriter(it, indent = " ")
            val errorRenderer = ErrorRenderer(true)
            testContainer(out, errorRenderer) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                }
            }
        }
        assertThat(captured).isEmpty()
    }

    @Test
    fun `it renders correctly if some tests failed`() {
        val captured = captureOutput {
            val out = IndentingOutputWriter(it, indent = " ")
            val errorRenderer = ErrorRenderer(false)
            testContainer(out, errorRenderer) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testSkipped("Test Two")
                    testPassed("Test Three")
                    testFailed("Test Four", RuntimeException("boom"))
                }
            }
        }
        assertThat(captured).isEqualTo(
            """
        |
        |1) Top level suite > Test Four
        |
        | java.lang.RuntimeException: boom
        |
        """.trimMargin()
        )
    }

    @Test
    fun `it renders correctly inline if some tests failed`() {
        val captured = captureOutput {
            val out = IndentingOutputWriter(it, indent = " ")
            val errorRenderer = ErrorRenderer(true)
            testContainer(out, errorRenderer) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testSkipped("Test Two")
                    testPassed("Test Three")
                    testFailed("Test Four", RuntimeException("boom"))
                }
            }
        }
        assertThat(captured).isEqualTo(
            """
        | java.lang.RuntimeException: boom
        |
        """.trimMargin()
        )
    }
}
