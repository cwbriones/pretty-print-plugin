package io.briones.gradle.output.test

import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.Style

private data class CapturedOutput(val style: Style, val content: String)

class CapturingOutputWriter : OutputWriter() {
    private val buffer: MutableList<CapturedOutput> = mutableListOf()
    private var style = Style.Plain
    private var content = StringBuilder()

    /**
     * Return the captured output along with its styling information, rendered inline
     * as HTML-style tags.
     */
    fun styledOutput(): String =
        buffer
            .plusElement(CapturedOutput(style, content.toString()))
            .joinToString(separator = "") {
                "<${it.style.name}>${it.content}</${it.style.name}>"
            }

    /** Return the plain-text output captured thus far. */
    fun unstyledOutput(): String =
        buffer
            .plusElement(CapturedOutput(style, content.toString()))
            .joinToString(separator = "") { it.content }

    override fun style(style: Style): CapturingOutputWriter {
        if (this.style != style) {
            buffer.add(CapturedOutput(this.style, content.toString()))
            this.style = style
            this.content = StringBuilder()
        }
        return this
    }

    override fun append(value: String): CapturingOutputWriter {
        content.append(value)
        return this
    }

    override fun println(value: String): CapturingOutputWriter {
        content.append(value).append('\n')
        return this
    }
}

fun captureOutput(
    block: (OutputWriter) -> Unit
): String = captureOutput({ it }, block)

fun <T: OutputWriter> captureOutput(
    init: (OutputWriter) -> T,
    block: (T) -> Unit
): String {
    val captor = CapturingOutputWriter()
    val out = init(captor)
    block(out)
    return captor.unstyledOutput()
}

fun captureStyledOutput(
    block: (OutputWriter) -> Unit
): String = captureStyledOutput({ it }, block)

fun <T: OutputWriter> captureStyledOutput(
    init: (OutputWriter) -> T,
    block: (T) -> Unit
): String {
    val captor = CapturingOutputWriter()
    val out = init(captor)
    block(out)
    return captor.styledOutput()
}
