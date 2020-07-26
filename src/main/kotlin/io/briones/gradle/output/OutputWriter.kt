package io.briones.gradle.output

interface OutputWriter {
    // Apply the Failure style.
    fun failure(): OutputWriter {
        return this
    }

    // Apply the Success style.
    fun success(): OutputWriter {
        return this
    }

    // Apply the Info style.
    fun info(): OutputWriter {
        return this
    }

    // Apply the Plain style.
    fun plain(): OutputWriter {
        return this
    }

    // Run the given lambda if the condition is `true`.
    //
    // This can be useful for applying conditional output while chaining.
    fun applyingIf(condition: Boolean, f: (OutputWriter) -> OutputWriter): OutputWriter {
        if (condition) {
            return f(this)
        }
        return this
    }

    fun append(value: String): OutputWriter {
        return this
    }

    fun println(value: String = ""): OutputWriter {
        return this
    }

    fun flush(): OutputWriter {
        return this
    }
}

