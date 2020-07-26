package io.briones.gradle.output

// An Output Writer that buffers its contents.
class BufferingOutputWriter(private var inner: OutputWriter) : OutputWriter {
    private val buffer: MutableList<BufferElement> = mutableListOf(BufferElement(Style.Plain))

    override fun failure(): OutputWriter = style(Style.Failure)

    override fun success(): OutputWriter = style(Style.Success)

    override fun info(): OutputWriter = style(Style.Info)

    override fun normal(): OutputWriter = style(Style.Plain)

    private fun style(style: Style): BufferingOutputWriter {
        if (buffer.lastOrNull()?.style != style) {
            buffer.add(BufferElement(style))
        }
        return this
    }

    override fun append(value: String): OutputWriter {
        buffer.last().line.append(value)
        return this
    }

    override fun println(value: String): OutputWriter {
        buffer.last().line.append(value)
        flush()
        return this
    }

    // Flush all output.
    override fun flush(): OutputWriter {
        for (elem in buffer) {
            when (elem.style) {
                Style.Success -> inner.success()
                Style.Failure -> inner.failure()
                Style.Info -> inner.info()
                Style.Plain -> inner.normal()
            }.append(elem.line.toString())
        }
        val style = buffer.last().style
        buffer.clear()
        buffer.add(BufferElement(style))
        return this
    }

    private class BufferElement(val style: Style, val line: StringBuilder = StringBuilder())
}