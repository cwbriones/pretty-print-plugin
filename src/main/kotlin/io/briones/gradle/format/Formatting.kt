package io.briones.gradle.format

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
@Suppress("MagicNumber")
fun humanReadableDuration(duration: Duration): String {
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
