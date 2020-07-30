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
    private var startOfLine = true
    private var indentLevel: Int = 0
    private val totalIndentLevel: Int
        get() = indentLevel + base

    private fun totalIndent(): String = indent.repeat(totalIndentLevel)

    fun increaseIndentation() {
        indentLevel++
    }

    fun decreaseIndentation() {
        require(indentLevel > 0) { "Indentation level is already 0" }
        indentLevel--
    }

    override fun style(style: Style): IndentingOutputWriter {
        inner = inner.style(style)
        return this
    }

    override fun append(value: String): IndentingOutputWriter {
        if (value == "") return this
        // Handle any values passed in having newlines.
        val lines = value.lineSequence().iterator()
        val first = lines.next()
        if (startOfLine && first != "") {
            inner = inner.append(totalIndent())
        }
        inner.append(first)
        startOfLine = first == ""
        while (lines.hasNext()) {
            val line = lines.next()
            inner.println()
            if (line != "") {
                inner.append(totalIndent())
            }
            inner.append(line)
            startOfLine = line == ""
        }
        return this
    }

    override fun println(value: String): IndentingOutputWriter {
        // Handle any values passed in having newlines.
        val lines = value.lineSequence().iterator()
        if (startOfLine) {
            inner = inner.append(totalIndent())
        }
        if (lines.hasNext()) {
            inner.println(lines.next())
        }
        for (line in lines) {
            inner = inner.append(totalIndent()).println(line)
        }
        startOfLine = true
        return this
    }

    /* Temporarily apply an extra level of indentation within the given block. */
    fun indented(f: IndentingOutputWriter.() -> Unit): IndentingOutputWriter {
        indentLevel++
        f(this)
        indentLevel--
        return this
    }

    override fun flush(): IndentingOutputWriter {
        inner.flush()
        return this
    }
}
