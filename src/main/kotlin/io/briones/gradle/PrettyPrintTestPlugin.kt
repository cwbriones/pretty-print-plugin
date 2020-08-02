package io.briones.gradle

import io.briones.gradle.format.TruncatingExceptionFormatter
import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.JColorOutputWriter
import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.UnstyledOutputWriter
import io.briones.gradle.render.ErrorRenderer
import io.briones.gradle.render.FailureCountingSymbols
import io.briones.gradle.render.SummarizingRenderer
import io.briones.gradle.render.Symbols
import io.briones.gradle.render.TestRenderer
import io.briones.gradle.render.defaultUnicodeSymbols
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

@Suppress("unused")
class PrettyPrintTestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<PrettyPrintTestExtension>("prettyPrint")

        project.afterEvaluate {
            applyOverrides(project, ext)
            val out = createOutputFactory(ext)
            var symbols: Symbols = defaultUnicodeSymbols
            if (!ext.inlineExceptions) {
                symbols = FailureCountingSymbols(symbols)
            }
            val renderer = ext.format.renderer(symbols)
            val renderers = mutableListOf(renderer)
            buildErrorRenderer(ext)?.let {
                renderers.add(it)
            }
            renderers.add(SummarizingRenderer(symbols))
            val listener = PrettyPrintListener(
                IndentingOutputWriter(out, indent = "  ", base = 1),
                renderers
            )
            project.tasks.withType<Test>().configureEach {
                testLogging {
                    setEvents(listOf<TestLogEvent>())
                }
                addTestListener(listener)
            }
        }
    }

    private fun buildErrorRenderer(
        ext: PrettyPrintTestExtension
    ): TestRenderer<IndentingOutputWriter>? {
        val formatter = TruncatingExceptionFormatter(
            showStackTraces = ext.showStackTraces,
            showCauses = ext.showCauses
        )
        if (!ext.showExceptions) {
            return null
        }
        return ErrorRenderer(
            ext.inlineExceptions,
            formatter
        )
    }

    private fun applyOverrides(project: Project, ext: PrettyPrintTestExtension) {
        withProperty(project, "prettyPrint.color") {
            ext.color = it == "true"
        }
        withProperty(project, "prettyPrint.format") {
            ext.formatName = it
        }
        withProperty(project, "prettyPrint.inlineExceptions") {
            ext.inlineExceptions = it == "true"
        }
    }

    private inline fun withProperty(
        project: Project,
        name: String,
        apply: (String) -> Unit
    ) {
        val prop = System.getProperty(name) ?: (project.properties[name] as String?)
        prop?.let(apply)
    }

    private fun createOutputFactory(ext: PrettyPrintTestExtension): OutputWriter {
        return if (ext.color) JColorOutputWriter(System.out) else UnstyledOutputWriter(System.out)
    }
}
