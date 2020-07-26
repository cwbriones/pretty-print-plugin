package io.briones.gradle.output

class IndentingOutputWriter(
    private var inner: OutputWriter,
    private val indent: String,
    private val base: Int = 0
) : OutputWriter() {
    var indentLevel: Int = 0
    var start = true

    private val totalIndent: Int
        get() = indentLevel + base

    override fun style(style: Style): OutputWriter = inner.style(style)

    override fun append(value: String): OutputWriter {
        val lines = value.lineSequence().iterator()
        if (start) {
            inner = inner.append(indent.repeat(totalIndent))
        }
        if (lines.hasNext()) {
            inner.append(lines.next())
        }
        for (line in lines) {
            inner = inner.println(indent.repeat(totalIndent)).append(line)
        }
        start = false
        return this
    }

    override fun println(value: String): OutputWriter {
        val lines = value.lineSequence().iterator()
        if (start) {
            inner = inner.append(indent.repeat(totalIndent))
        }
        if (lines.hasNext()) {
            inner.println(lines.next())
        }
        for (line in lines) {
            inner = inner.append(indent.repeat(totalIndent)).println(line)
        }
        start = true
        return this
    }

    override fun flush(): OutputWriter {
        inner.flush()
        return this
    }
}