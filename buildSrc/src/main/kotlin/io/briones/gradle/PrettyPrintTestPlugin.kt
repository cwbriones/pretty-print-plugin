package io.briones.gradle

import io.briones.gradle.format.TreePrintingListener
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.withType
import javax.inject.Inject

class PrettyPrintTestPlugin @Inject constructor(
    private val outputFactory: StyledTextOutputFactory
) : Plugin<Project> {
    override fun apply(project: Project) {
        val out = outputFactory.create(javaClass)
        project.tasks.withType<Test>().configureEach {
            testLogging {
                setEvents(listOf<TestLogEvent>())
            }
            addTestListener(TreePrintingListener(out))
        }
    }
}

