package io.briones.gradle.output

import com.google.common.truth.Truth.assertThat
import io.briones.gradle.output.test.captureOutput
import io.briones.gradle.output.test.captureStyledOutput
import org.junit.jupiter.api.Test

class OutputWriterTest {
    @Test
    fun `it should only apply the block if the condition is true`() {
        val captured = captureOutput {
            it.applyIf(true) { out ->
                out.append("one")
            }.applyIf(false) { out ->
                out.append("hello")
            }.applyIf(true) { out ->
                out.append("two")
            }.applyIf(false) { out ->
                out.append("world")
            }
        }
        assertThat(captured).isEqualTo("onetwo")
    }

    @Test
    fun `it should apply the correct style`() {
        val captured = captureStyledOutput {
            it.append("plain")
                .style(Style.Success).append("success")
                .style(Style.Info).append("info")
                .style(Style.Failure).append("failure")
                .style(Style.Bold).append("bold")
                .style(Style.Plain).append("plain")
        }
        assertThat(captured).isEqualTo(
            "<Plain>plain</Plain><Success>success</Success>" +
                "<Info>info</Info><Failure>failure</Failure>" +
                "<Bold>bold</Bold><Plain>plain</Plain>"
        )
    }

    @Test
    fun `it should apply the style with the given extension`() {
        val captured = captureStyledOutput {
            it.append("plain")
                .success().append("success")
                .info().append("info")
                .failure().append("failure")
                .bold().append("bold")
                .plain().append("plain")
        }
        assertThat(captured).isEqualTo(
            "<Plain>plain</Plain><Success>success</Success>" +
                "<Info>info</Info><Failure>failure</Failure>" +
                "<Bold>bold</Bold><Plain>plain</Plain>"
        )
    }
}
