package io.briones.gradle.output

import com.diogonunes.jcolor.AnsiFormat
import com.diogonunes.jcolor.Attribute
import java.io.PrintStream

/**
 * An output writer that uses ANSI color codes for its styling via JColor.
 */
class JColorOutputWriter(private val out: PrintStream) : OutputWriter() {
    private var format: AnsiFormat = AnsiFormat(Attribute.NONE())

    companion object {
        private const val ESC = 27.toChar()
        private const val ERASE_TO_END = "$ESC[K"
    }

    private val styleMapping = mapOf(
        Style.Failure to AnsiFormat(Attribute.RED_TEXT()),
        Style.Success to AnsiFormat(Attribute.GREEN_TEXT()),
        Style.Info to AnsiFormat(Attribute.YELLOW_TEXT()),
        Style.Plain to AnsiFormat(Attribute.NONE()),
        Style.Bold to AnsiFormat(Attribute.BLACK_TEXT())
    )

    override fun style(style: Style): JColorOutputWriter {
        format = styleMapping[style] ?: throw IllegalStateException("Unhandled variant $style")
        return this
    }

    override fun append(value: String): JColorOutputWriter {
        out.print(format.format(value))
        return this
    }

    override fun println(value: String): JColorOutputWriter {
        // JColor doesn't handle screen-clearing directives
        out.print(ERASE_TO_END)
        out.println(format.format(value))
        return this
    }

    override fun flush(): JColorOutputWriter {
        out.flush()
        return this
    }
}
