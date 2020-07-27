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
            require(value.isNotEmpty()) { "Format name cannot be empty." }
            val normalized = value.toLowerCase().toCharArray().let {
                it[0] = it[0].toUpperCase()
                it.joinToString(separator="")
            }
            try {
                format = Format.valueOf(normalized)
            } catch (e: IllegalArgumentException) {
                val formats = Format.values().joinToString { "'${it.name.toLowerCase()}'" }
                throw IllegalArgumentException("Invalid format '$value', valid formats: $formats")
            }
        }
}

