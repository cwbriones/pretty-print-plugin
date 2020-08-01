package io.briones.gradle

import io.briones.gradle.format.ErrorPrintingReporter
import io.briones.gradle.format.PrettyPrintingListener
import io.briones.gradle.format.SummarizingReporter
import io.briones.gradle.output.IndentingOutputWriter
import io.briones.gradle.output.JColorOutputWriter
import io.briones.gradle.output.OutputWriter
import io.briones.gradle.output.UnstyledOutputWriter
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
            val reporter = ext.format.listener()
            val inlineExceptions = ext.inlineExceptions && ext.format.supportsInlineExceptions()
            val reporters = listOf(
                reporter,
                ErrorPrintingReporter(inlineExceptions),
                SummarizingReporter()
            )
            val listener = PrettyPrintingListener(
                IndentingOutputWriter(out, indent = "  ", base = 1),
                reporters
            )
            project.tasks.withType<Test>().configureEach {
                testLogging {
                    setEvents(listOf<TestLogEvent>())
                }
                addTestListener(listener)
            }
        }
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

