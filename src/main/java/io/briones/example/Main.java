package io.briones.example;

import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.SystemPrintln")
@DefaultAnnotationForParameters(NonNull.class)
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final GreetingConfig config = ImmutableGreetingConfig.builder()
            .prefix("Hello")
            .name("World")
            .build();

        logger.info("Hello, World!");
//        logger.atInfo()
//                .addKeyValue("foo", "bar")
//                .addKeyValue("bar", Map.of(
//                        "ok", 1
//                ))
//                .addKeyValue("baz", Map.of(
//                        "why", List.of(1, 2, 3)
//                ))
//                .log("Hello, world!");

        final String prefix = config.prefix().orElse("");
        System.out.println(String.format("%s, %s!", prefix, config.name()));
    }
}
