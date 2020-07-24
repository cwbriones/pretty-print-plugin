package io.briones.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MainTest {
    @Test
    public void it_should_work() {
        assertThat("this").isNotEqualTo("that");
    }

    @Test
    public void this_should_also_work() {
        assertThat("this").isNotEqualTo("that");
    }

    @Nested
    public class GivenAnInnerClass {
        @Test
        @Disabled
        public void disable_test_should_be_skipped() {
            assertThat("this").isNotEqualTo("that");
        }
    }
}