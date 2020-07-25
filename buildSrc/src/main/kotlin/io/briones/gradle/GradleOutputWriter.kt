package io.briones.gradle

import org.gradle.internal.logging.text.StyledTextOutput

class GradleOutputWriter(private var out: StyledTextOutput) : OutputWriter {
    override fun failure(): OutputWriter {
        out = out.style(StyledTextOutput.Style.Failure)
        return this
    }

    override fun success(): OutputWriter {
        out = out.style(StyledTextOutput.Style.Success)
        return this
    }

    override fun info(): OutputWriter {
        out = out.style(StyledTextOutput.Style.Info)
        return this
    }

    override fun normal(): OutputWriter {
        // Why can't I use "withStyle" ?
        out = out.style(StyledTextOutput.Style.Normal)
        return this
    }

    override fun append(value: String): OutputWriter {
        out = out.append(value)
        return this
    }

    override fun println(value: String): OutputWriter {
        out = out.println(value)
        return this
    }
}