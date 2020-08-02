package io.briones.gradle.format

import java.io.PrintWriter
import java.io.StringWriter

/** An ExceptionFormatter provides a mechanism for formatting exception messages for display. */
interface ExceptionFormatter {
    /**
     * Format the given `Throwable`.
     *
     * `classFilter` may be used to truncate the stack trace, if necessary.
     */
    fun format(e: Throwable, classFilter: String? = null): String
}

/** A formatter that just calls `e.toString()` */
class SimpleExceptionFormatter : ExceptionFormatter {
    override fun format(e: Throwable, classFilter: String?): String {
        return e.toString()
    }
}

class TruncatingExceptionFormatter(
    private val showCauses: Boolean,
    private val showStackTraces: Boolean
) : ExceptionFormatter {
    override fun format(e: Throwable, classFilter: String?): String {
        truncateStackTrace(e, showStackTraces, null)
        val cause = e.cause
        if (cause != null) {
            truncateStackTrace(cause, showStackTraces, classFilter)
        }
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        val formatted = sw.toString().replace("\t", "    ").trimEnd()
        if (showCauses) {
            return formatted
        }
        return formatted
            .lines()
            .takeWhile { !it.startsWith("Caused by:") }
            .joinToString(separator = "\n")
    }

    private fun truncateStackTrace(e: Throwable, showStackTraces: Boolean, classFilter: String?) {
        val end = e.stackTrace
            .takeWhile {
                showStackTraces &&
                    !it.isNativeMethod && // This doesn't necessarily need to be filtered out.
                    (classFilter == null || it.className != classFilter)
            }
            .count()
        e.stackTrace = e.stackTrace.copyOfRange(0, end)
    }
}
