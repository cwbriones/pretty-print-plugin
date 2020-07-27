package io.briones.gradle

import io.briones.gradle.format.Format
import org.gradle.api.Project
import java.lang.IllegalArgumentException

open class PrettyPrintTestExtension {
    var color: Boolean = true
    var format: Format = Format.Mocha

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
                throw IllegalArgumentException("Invalid format '$value'")
            }
        }
}
