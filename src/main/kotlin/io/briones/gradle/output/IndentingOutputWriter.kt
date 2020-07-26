package io.briones.gradle.output

class IndentingOutputWriter(
    private var out: OutputWriter,
    private val indent: String,
    private val base: Int = 0
) : OutputWriter {
    var indentLevel: Int = 0
    var start = true

    private val totalIndent: Int
        get() = indentLevel + base

    override fun append(value: String): OutputWriter {
        val lines = value.lineSequence().iterator()
        if (start) {
            out = out.append(indent.repeat(totalIndent))
        }
        if (lines.hasNext()) {
            out.append(lines.next())
        }
        for (line in lines) {
            out = out.println(indent.repeat(totalIndent)).append(line)
        }
        start = false
        return this
    }

    override fun println(value: String): OutputWriter {
        val lines = value.lineSequence().iterator()
        if (start) {
            out = out.append(indent.repeat(totalIndent))
        }
        if (lines.hasNext()) {
            out.println(lines.next())
        }
        for (line in lines) {
            out = out.append(indent.repeat(totalIndent)).println(line)
        }
        start = true
        return this
    }

    override fun failure(): OutputWriter {
        out = out.failure()
        return this
    }

    override fun success(): OutputWriter {
        out = out.success()
        return this
    }

    override fun info(): OutputWriter {
        out = out.info()
        return this
    }

    override fun normal(): OutputWriter {
        out = out.normal()
        return this
    }
}