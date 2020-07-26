package io.briones.gradle.output

/**
 * An output writer that applies indentation to the start of each line.
 *
 * The indentation amount can be modified after the writer has been created.
 */
class IndentingOutputWriter(
    private var inner: OutputWriter,
    private val indent: String,
    private val base: Int = 0
) : OutputWriter() {
    var indentLevel: Int = 0
        set(value) {
            require(value >= 0) { "Indentation level is negative: $value" }
            field = value
        }

    private var startOfLine = true

    private val totalIndent: Int
        get() = indentLevel + base

    override fun style(style: Style): IndentingOutputWriter {
        inner = inner.style(style)
        return this
    }

    override fun append(value: String): IndentingOutputWriter {
        // Handle any values passed in having newlines.
        val lines = value.lineSequence().iterator()
        if (startOfLine) {
            inner = inner.append(indent.repeat(totalIndent))
        }
        if (lines.hasNext()) {
            inner.append(lines.next())
        }
        for (line in lines) {
            inner = inner.println(indent.repeat(totalIndent)).append(line)
        }
        startOfLine = false
        return this
    }

    override fun println(value: String): IndentingOutputWriter {
        // Handle any values passed in having newlines.
        val lines = value.lineSequence().iterator()
        if (startOfLine) {
            inner = inner.append(indent.repeat(totalIndent))
        }
        if (lines.hasNext()) {
            inner.println(lines.next())
        }
        for (line in lines) {
            inner = inner.append(indent.repeat(totalIndent)).println(line)
        }
        startOfLine = true
        return this
    }

    override fun flush(): IndentingOutputWriter {
        inner.flush()
        return this
    }
}