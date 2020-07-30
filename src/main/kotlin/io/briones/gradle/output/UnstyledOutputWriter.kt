package io.briones.gradle.output

import java.io.PrintStream

/**
 * An output writer that ignores any styles applied to it and writes received
 * text as-is to the underlying `PrintStream`.
 */
class UnstyledOutputWriter(private val out: PrintStream) : OutputWriter() {
    override fun style(style: Style): UnstyledOutputWriter = this

    override fun append(value: String): UnstyledOutputWriter {
        out.print(value)
        return this
    }

    override fun println(value: String): UnstyledOutputWriter {
        out.println(value)
        return this
    }

    override fun flush(): UnstyledOutputWriter {
        out.flush()
        return this
    }
}
