package io.briones.gradle.output

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.output.test.CapturingOutputWriter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class IndentingOutputWriterTest {
    private val INDENT = " >>"

    @Test
    fun `the basics`() {
        val captured = executeAndCapture(INDENT, 1) { out ->
            out.append("Hello ").println("World!")
            out.increaseIndentation()
            out.increaseIndentation()
            out.println("Onwards and rightwards")
            out.decreaseIndentation()
            out.println("Reel it back in")
        }

        assertThat(captured).isEqualTo("""
           | >>Hello World!
           | >> >> >>Onwards and rightwards
           | >> >>Reel it back in
           |
        """.trimMargin())
    }

    @Test
    fun `it should throw when decreasing indentation below base`() {
        val out = IndentingOutputWriter(
            inner = CapturingOutputWriter(),
            indent = INDENT,
            base = 42
        )
        repeat(20) {
            out.increaseIndentation()
        }
        repeat(20) {
            out.decreaseIndentation()
        }
        assertThrows<IllegalArgumentException>(out::decreaseIndentation)
    }

    @Test
    fun `it should handle newlines within append`() {
        val captured = executeAndCapture(INDENT, 1) {
            it.append("Hello, World!\nThis line should be properly").append(" indented")
                .append("\nand so should ").append("this one.\n")
                .append("Here's two\nfinal lines\n")
        }

        assertThat(captured).isEqualTo("""
            | >>Hello, World!
            | >>This line should be properly indented
            | >>and so should this one.
            | >>Here's two
            | >>final lines
            |
        """.trimMargin("|"))
    }

    @Test
    fun `it should handle newlines within println`() {
        val captured = executeAndCapture(INDENT, base = 1) { out ->
            out.println("One line\ncan actually be\nthree lines")
        }
        assertThat(captured).isEqualTo("""
            | >>One line
            | >>can actually be
            | >>three lines
            |
        """.trimMargin("|"))
    }

    private fun executeAndCapture(
        indent: String,
        base: Int,
        f: (IndentingOutputWriter) -> Unit
    ): String {
        val captor = CapturingOutputWriter()
        val out = IndentingOutputWriter(
            inner = captor,
            indent = indent,
            base = base
        )
        f(out)
        return captor.unstyledOutput()
    }
}

