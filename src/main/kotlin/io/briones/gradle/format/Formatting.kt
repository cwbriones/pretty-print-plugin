package io.briones.gradle.format

import org.gradle.api.tasks.testing.TestDescriptor
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

    val secondsPart = duration.toSecondsPart()
    val millisPart = duration.toMillisPart()
    val segments = mutableListOf<String>()
    if (duration.toHours() > 0) {
        segments.add("${duration.toHours()}h")
    }
    if (duration.toMinutesPart() > 0) {
        segments.add("${duration.toMinutesPart()}m")
    }
    val finalSegment = when {
        duration > Duration.ofMinutes(1) -> "${secondsPart}s"
        duration < Duration.ofSeconds(1) -> "${millisPart}ms"
        else -> {
            val tenths = duration.toMillisPart() / 100
            "${duration.toSecondsPart()}.${tenths}s"
        }
    }
    segments.add(finalSegment)
    return segments.joinToString(separator = " ")
}

/**
 * Return the fully-qualified display name of this test descriptor
 *
 * This prepends the display name of all enclosing suites, e.g.
 *
 * `MySuite > WhenTheresAnEnclosingClass > itsIncludedInTheName()`
 */
fun TestDescriptor.fqDisplayName(separator: String = " > "): String {
    return generateSequence(this) { it.parent }
        .filter { it.className != null }
        .map { it.displayName }
        .toList()
        .reversed()
        .joinToString(separator = separator)
}

fun formattedStackTrace(e: Throwable, className: String?): String {
    truncateStackTrace(e, className)
    e.cause?.let { truncateStackTrace(it) }
    val sw = StringWriter()
    e.printStackTrace(PrintWriter(sw))
    return sw.toString().trim()
}

private fun truncateStackTrace(e: Throwable, className: String? = null) {
    val end = e.stackTrace
        .takeWhile { s -> !s.isNativeMethod }
        .takeWhile { s -> className == null || s.className == className }
        .count()

    e.stackTrace = e.stackTrace.copyOfRange(0, end)
}
