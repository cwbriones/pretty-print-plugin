package io.briones.gradle.test

import org.junit.jupiter.api.DisplayNameGenerator
import java.lang.reflect.Method

class DefaultDisplayNameGenerator : DisplayNameGenerator.Standard() {
    override fun generateDisplayNameForMethod(testClass: Class<*>?, testMethod: Method?): String {
        requireNotNull(testMethod) { "Guaranteed by the JUnit implementation."}
        // Omit the parens
        return testMethod.name
    }
}

