package io.briones.gradle.render

import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import java.time.Duration

fun TestResult.getDuration(): Duration = Duration.ofMillis(this.endTime - this.startTime)

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
