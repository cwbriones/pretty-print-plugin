package io.briones.gradle.output

interface OutputWriter {
    fun failure(): OutputWriter {
        return this
    }

    fun success(): OutputWriter {
        return this
    }

    fun info(): OutputWriter {
        return this
    }

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

    fun plain(): OutputWriter {
        return this
    }

    fun flush(): OutputWriter {
        return this
    }
}

