package io.briones.example;

import java.util.Optional;
import org.immutables.value.Value;


/**
 * Application Configuration.
 */
@Value.Immutable
@Value.Style(
    optionalAcceptNullable = true
)
public interface GreetingConfig {
    /**
     * The message prefix.
     */
    Optional<String> prefix();

    /**
     * The name to greet.
     */
    String name();
}
