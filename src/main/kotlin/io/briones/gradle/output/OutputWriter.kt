package io.briones.gradle.output

interface OutputWriter {
    // Apply the Failure style.
    fun failure(): OutputWriter = this

    // Apply the Success style.
    fun success(): OutputWriter = this

    // Apply the Info style.
    fun info(): OutputWriter = this

    // Apply the Plain style.
    fun plain(): OutputWriter = this

    // Apply the Bold style.
    fun bold(): OutputWriter = this

    // Run the given lambda if the condition is `true`.
    //
    // This can be useful for applying conditional output while chaining.
    fun applyingIf(condition: Boolean, f: (OutputWriter) -> OutputWriter): OutputWriter =
        if (condition) {
            f(this)
        } else {
            this
        }

    fun append(value: String): OutputWriter = this

    fun println(value: String = ""): OutputWriter = this

    fun flush(): OutputWriter = this
}

