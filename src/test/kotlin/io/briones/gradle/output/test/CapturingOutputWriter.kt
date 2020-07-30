package io.briones.gradle.output.test

import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.Style

class CapturingOutputWriter : OutputWriter() {
    private val buffer: MutableList<CapturedOutput> = mutableListOf()
    private var style = Style.Plain
    private var content = StringBuilder()

    class CapturedOutput(val style: Style, val content: String)

    /** Return the captured output along with its styling information */
    fun styledOutput(): List<CapturedOutput> =
        buffer
            .plusElement(CapturedOutput(style, content.toString()))
            .toList()

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