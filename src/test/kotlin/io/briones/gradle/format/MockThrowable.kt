package io.briones.gradle.format

fun dummyThrowable(msg: String, n: Int): Throwable = try {
    dummyStackTraceN(msg, n)
} catch(e: Exception) {
    e
}

private fun dummyStackTraceN(msg: String, n: Int): Nothing =
    if (n == 0) {
        throw RuntimeException(msg)
    } else {
        dummyStackTraceN(msg, n - 1)
    }
