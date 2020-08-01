package io.briones.gradle.format

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.format.test.testContainer
import io.briones.gradle.output.test.captureOutput
import org.junit.jupiter.api.Test

class TreePrintingListenerTest {
    @Test
    fun `test all passed`() {
        val captured = captureOutput {
            val listener = TreePrintingListener(it, true)
            testContainer(listener) {
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
        assertThat("""
        |  
        |  Top level suite
        |    ✓ Test One (50ms)
        |    ✓ Test Two (50ms)
        |    ✓ Test Three (50ms)
        |    Nested
        |      ✓ Nested One (50ms)
        |      ✓ Nested Two (50ms)
        |    ✓ Test Four (50ms)
        |    ✓ Test Five (50ms)
        |    ✓ Test Six (50ms)
        |  Top level suite 2
        |    ✓ Test One (50ms)
        |    ✓ Test Two (50ms)
        |    ✓ Test Three (50ms)
        |  
        |  
        |  ✓ 11 passing (-550ms)
        |
        """.trimMargin()).isEqualTo(captured)
    }

    @Test
    fun `test inline failure`() {
        val captured = captureOutput {
            val listener = TreePrintingListener(it, true)
            testContainer(listener) {
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
        assertThat("""
        |  
        |  Top level suite
        |    ✓ Test One (50ms)
        |    ✓ Test Two (50ms)
        |    ✗ Failing Test (50ms)
        |      java.lang.RuntimeException: boom
        |    ✓ Test Three (50ms)
        |  Top level suite 2
        |    ✓ Test One (50ms)
        |    ✓ Test Two (50ms)
        |    ✓ Test Three (50ms)
        |  
        |  
        |  6 passing (-350ms)
        |  1 failing
        |
        """.trimMargin()).isEqualTo(captured)
    }

    @Test
    fun `test post-reporting failure`() {
        val captured = captureOutput {
            val listener = TreePrintingListener(it, false)
            testContainer(listener) {
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
        assertThat("""
        |  
        |  Top level suite
        |    ✓ Test One (50ms)
        |    ✓ Test Two (50ms)
        |    ✗ Failing Test (50ms)
        |    ✓ Test Three (50ms)
        |  Top level suite 2
        |    ✓ Test One (50ms)
        |    ✓ Test Two (50ms)
        |    ✓ Test Three (50ms)
        |  
        |  
        |  1) Top level suite > Failing Test
        |  
        |    java.lang.RuntimeException: boom
        |  
        |  
        |  6 passing (-350ms)
        |  1 failing
        |
        """.trimMargin()).isEqualTo(captured)
    }
}

