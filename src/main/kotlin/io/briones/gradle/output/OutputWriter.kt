package io.briones.gradle.output

abstract class OutputWriter {
    // Apply the Failure style.
    fun failure(): OutputWriter = style(Style.Failure)

    // Apply the Success style.
    fun success(): OutputWriter = style(Style.Success)

    // Apply the Info style.
    fun info(): OutputWriter = style(Style.Info)

    // Apply the Plain style.
    fun plain(): OutputWriter = style(Style.Plain)

    // Apply the Bold style.
    fun bold(): OutputWriter = style(Style.Bold)

    // Run the given lambda if the condition is `true`.
    //
    // This can be useful for applying conditional output while chaining.
    fun applyingIf(condition: Boolean, f: (OutputWriter) -> OutputWriter): OutputWriter =
        if (condition) {
            f(this)
        } else {
            this
        }

    // Apply the given style to this OutputWriter.
    abstract fun style(style: Style): OutputWriter

    // Append the given value to this OutputWriter.
    open fun append(value: String): OutputWriter = this

    // Append the given value to this OutputWriter and write to a new line.
    open fun println(value: String = ""): OutputWriter = this

    open fun flush(): OutputWriter = this
}

