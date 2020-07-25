package io.briones.example;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.joining;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(CamelCaseDisplayNameGenerator.class)
public class MainTest {
    @Test
    public void itShouldAlsoWork() {
        assertThat("this").isNotEqualTo("that");
    }

    @Test
    public void thisShouldWork() {
        assertThat("this").isNotEqualTo("that");
    }

    @Test
    @Disabled
    public void disabledTestShouldBeSkipped() {
        assertThat("this").isNotEqualTo("that");
    }

    @Nested
    public class whenTheresAnInnerClass {
        @Test
        public void itShowsTheTruthExample() {
            var corelibs = List.of("dagger", "auto", "caliper", "guava");
            assertThat(corelibs).containsExactly("guava", "dagger", "truth", "auto", "caliper");
        }

        @Test
        public void itShouldDoTheThing() {
            assertThat("this").isNotEqualTo("that");
        }
    }

    @Nested
    public class GivenAnotherInnerClass {
        @Test
        public void itShouldNotFail() {
            assertThat("this").isNotEqualTo("that");
        }

        @Test
        public void itShowsTheCauseOfTheFailure() {
            try {
                throw new IOException("boom");
            } catch (Exception e) {
                throw new RuntimeException("It blew up", e);
            }
        }
    }
}

class CamelCaseDisplayNameGenerator implements DisplayNameGenerator {
    @Override
    public String generateDisplayNameForClass(final Class<?> testClass) {
        return testClass.getSimpleName();
    }

    @Override
    public String generateDisplayNameForNestedClass(final Class<?> nestedClass) {
        return getDisplayName(nestedClass.getSimpleName());
    }

    @Override
    public String generateDisplayNameForMethod(final Class<?> testClass, final Method testMethod) {
        return getDisplayName(testMethod.getName());
    }

    final static Pattern pattern = Pattern.compile("((?=[A-Z])|_)");

    private static String getDisplayName(final String ident) {
        return Stream.of(pattern.split(ident))
                .map(String::toLowerCase)
                .collect(joining(" "));
    }

    private static List<String> splitCamelCase(final String ident) {
        int i = 0;
        var words = new ArrayList<String>();
        while (i < ident.length()) {
            int j = i;
            while (j < ident.length() && Character.isLowerCase(ident.charAt(j))) {
                j++;
            }
//            var end = j < ident.length() ? j : j - 1;
            words.add(ident.substring(i, j).toLowerCase());
            i = j + 1;
        }
        return words;
    }
}
