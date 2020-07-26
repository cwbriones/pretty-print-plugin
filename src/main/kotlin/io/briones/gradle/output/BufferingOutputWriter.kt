package io.briones.gradle.output

/**
 * An Output Writer that buffers its contents.
 */
class BufferingOutputWriter(private var inner: OutputWriter) : OutputWriter() {
    private val buffer: MutableList<BufferElement> = mutableListOf(BufferElement(Style.Plain))

    override fun style(style: Style): BufferingOutputWriter {
        if (buffer.lastOrNull()?.style != style) {
            buffer.add(BufferElement(style))
        }
        return this
    }

    override fun append(value: String): BufferingOutputWriter {
        buffer.last().line.append(value)
        return this
    }

    override fun println(value: String): BufferingOutputWriter {
        buffer.last().line.append(value)
        flush()
        return this
    }

    override fun flush(): BufferingOutputWriter {
        for (elem in buffer) {
            when (elem.style) {
                Style.Success -> inner.success()
                Style.Failure -> inner.failure()
                Style.Info -> inner.info()
                Style.Plain -> inner.plain()
                Style.Bold -> inner.bold()
            }.append(elem.line.toString())
        }
        val style = buffer.last().style
        buffer.clear()
        buffer.add(BufferElement(style))
        return this
    }

    private class BufferElement(val style: Style, val line: StringBuilder = StringBuilder())
}