package io.briones.example;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotationForParameters(NonNull.class)
public class Main {
    public static void main(String[] args) {
        final GreetingConfig config = ImmutableGreetingConfig.builder()
            .prefix("Hello")
            .name("World")
            .build();
        final String prefix = config.prefix().orElse("");
        System.out.println(String.format("%s, %s!", prefix, config.name()));
    }
}
