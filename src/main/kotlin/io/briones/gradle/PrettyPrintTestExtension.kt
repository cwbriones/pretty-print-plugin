package io.briones.gradle

import io.briones.gradle.format.Format
import java.lang.IllegalArgumentException

open class PrettyPrintTestExtension {
    var color: Boolean = true
    var format: Format = Format.Mocha
    var inlineExceptions: Boolean = false

    var formatName: String
        get() {
            return format.toString().toLowerCase()
        }
        set(value) {
            require(value.isNotBlank()) { "Format name cannot be empty." }
            val valid = Format.values().joinToString { "'${it.name.toLowerCase()}'" }
            val normalized = value.toLowerCase()
            format = Format.values().find { it.name.toLowerCase() == normalized } ?:
                throw IllegalArgumentException("Invalid format '$value', valid formats: $valid")
        }
}

