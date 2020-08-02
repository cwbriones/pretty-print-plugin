package io.briones.gradle

import io.briones.gradle.format.Format

open class PrettyPrintTestExtension {
    var color: Boolean = true
    var format: Format = Format.Mocha
        set(value) {
            field = value
            if (!value.supportsInlineExceptions()) {
                inlineExceptions = false
            }
        }

    var inlineExceptions: Boolean = false
        set(value) {
            field = value
            if (value && !format.supportsInlineExceptions()) {
                field = false
            }
        }

    var showExceptions: Boolean = true
    var showStackTraces: Boolean = true
    var showCauses: Boolean = true

    var formatName: String
        get() {
            return format.toString().toLowerCase()
        }
        set(value) {
            require(value.isNotBlank()) { "Format name cannot be empty." }
            val valid = Format.values().joinToString { "'${it.name.toLowerCase()}'" }
            val normalized = value.toLowerCase()
            format = Format.values().find { it.name.toLowerCase() == normalized }
                ?: throw IllegalArgumentException("Invalid format '$value', valid formats: $valid")
        }
}
