package io.briones.gradle.format

import org.gradle.api.tasks.testing.TestResult
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Duration

/** Return a string that contains the given lines surrounded by a box. */
fun joinInBox(vararg lines: String): String {
    val lineLength = lines.map { it.length }.max()!!

    val bordered = mutableListOf<String>()
    bordered.add("┌${"─".repeat(lineLength + 2)}┐")
    lines.forEach {
        val padded = it.padEnd(lineLength, ' ')
        bordered.add("│ $padded │")
    }
    bordered.add("└${"─".repeat(lineLength + 2)}┘")
    return bordered.joinToString("\n", postfix = "\n")
}

/** Return the duration as a human-readable string. e.g `123000ms` is formatted as `2m 3s` */
fun TestResult.humanReadableDuration(): String {
    val duration = Duration.ofMillis(this.endTime - this.startTime)
    if (duration < Duration.ofSeconds(1)) {
        return "${duration.toMillis()}ms"
    }
    val display = StringBuilder()
    val minutes = duration.toMinutesPart()
    if (minutes > 0) {
        display.append(minutes).append('m')
    }
    val seconds = duration.toSecondsPart()
    display.append(' ').append(seconds)
    if (minutes == 0) {
        val decimalSeconds = duration.toMillisPart() / 100
        display.append('.').append(decimalSeconds)
    }
    display.append('s')
    return display.toString()
}

fun formattedStackTrace(e: Throwable, className: String?): String {
    truncateStackTrace(e, className)
    e.cause?.let { truncateStackTrace(it) }
    val sw = StringWriter()
    e.printStackTrace(PrintWriter(sw))
    return sw.toString().trim()
}

private fun truncateStackTrace(e: Throwable, className: String? = null) {
    val end = sequenceOf(*e.stackTrace)
        .takeWhile { s -> !s.isNativeMethod }
        .takeWhile { s -> className == null || s.className == className }
        .count()

    e.stackTrace = e.stackTrace.copyOfRange(0, end)
}
