package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.testContainer
import io.briones.gradle.output.test.captureOutput
import org.junit.jupiter.api.Test

class DotPrintingListenerTest {
    @Test
    fun `it renders correctly when all tests passed`() {
        val captured = captureOutput {
            val dotListener = DotPrintingListener(it)
            testContainer(dotListener) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testPassed("Test Two")
                    testPassed("Test Three")
                }
            }
        }
        assertThat(captured).isEqualTo("""
        |  
        |  ...
        |  
        |  ✓ 3 passing (-150ms)
        |
        """.trimMargin())
    }

    @Test
    fun `it renders correctly some tests failed`() {
        val captured = captureOutput {
            val dotListener = DotPrintingListener(it)
            testContainer(dotListener) {
                suite("Top level suite") {
                    testPassed("Test One")
                    testSkipped("Test Two")
                    testPassed("Test Three")
                    testFailed("Test Four", RuntimeException("boom"))
                }
            }
        }
        assertThat("""
        |  
        |  .s.X
        |  
        |  1) Top level suite > Test Four
        |  
        |    java.lang.RuntimeException: boom
        |  
        |  
        |  2 passing (-200ms)
        |  1 failing
        |  1 skipped
        |
        """.trimMargin()).isEqualTo(captured)
    }
}