package io.briones.gradle

import io.briones.gradle.format.TreePrintingListener
import io.briones.gradle.output.JColorOutputWriter
import io.briones.gradle.output.OutputWriter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType

@Suppress("unused")
class PrettyPrintTestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val out = createOutputFactory()
        project.tasks.withType<Test>().configureEach {
            testLogging {
                setEvents(listOf<TestLogEvent>())
            }
            addTestListener(TreePrintingListener(out))
        }
    }

    private fun createOutputFactory(): OutputWriter =
        JColorOutputWriter(System.out)
}

