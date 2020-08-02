package io.briones.gradle.output

abstract class OutputWriter {
    /** Apply the given style to this OutputWriter. */
    abstract fun style(style: Style): OutputWriter

    /** Append the given value to this OutputWriter. */
    abstract fun append(value: String): OutputWriter

    /** Append the given value to this OutputWriter and write to a new line. */
    abstract fun println(value: String = ""): OutputWriter

    /**
     * Flush this OutputWriter.
     *
     * This has no effect for unbuffered writers.
     */
    open fun flush(): OutputWriter = this
}

/**
 * Run the given lambda if the condition is `true`.
 *
 * This can be useful for applying conditional output while chaining builders.
 */
fun <T> T.applyIf(condition: Boolean, f: (T) -> T): T =
    if (condition) {
        f(this)
    } else {
        this
    }

/** Apply the Failure style. */
fun <T : OutputWriter> T.failure(): T {
    style(Style.Failure)
    return this
}

/** Apply the Success style. */
fun <T : OutputWriter> T.success(): T {
    style(Style.Success)
    return this
}

/** Apply the Info style. */
fun <T : OutputWriter> T.info(): T {
    style(Style.Info)
    return this
}

/** Apply the Plain style. */
fun <T : OutputWriter> T.plain(): OutputWriter {
    style(Style.Plain)
    return this
}

/** Apply the Bold style. */
fun <T : OutputWriter> T.bold(): OutputWriter {
    style(Style.Bold)
    return this
}
