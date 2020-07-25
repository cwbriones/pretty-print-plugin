package io.briones.gradle

class IndentingOutputWriter(
    private var out: OutputWriter,
    private val indent: String
) : OutputWriter {
    var indentLevel: Int = 0
    var start = true

    override fun append(value: String): OutputWriter {
        val lines = value.lineSequence().iterator()
        if (start) {
            out = out.append(indent.repeat(indentLevel))
        }
        if (lines.hasNext()) {
            out.append(lines.next())
        }
        for (line in lines) {
            out = out.println(indent.repeat(indentLevel)).append(line)
        }
        start = false
        return this
    }

    override fun println(value: String): OutputWriter {
        val lines = value.lineSequence().iterator()
        if (start) {
            out = out.append(indent.repeat(indentLevel))
        }
        if (lines.hasNext()) {
            out.println(lines.next())
        }
        for (line in lines) {
            out = out.append(indent.repeat(indentLevel)).println(line)
        }
        start = true
        return this
    }

    fun indentLevel(level: Int): IndentingOutputWriter {
        indentLevel = level
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
        // Why can't I use "withStyle" ?
        out = out.normal()
        return this
    }
}