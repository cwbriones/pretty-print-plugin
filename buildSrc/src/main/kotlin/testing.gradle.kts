plugins {
    java
}
apply<io.briones.gradle.PrettyPrintTestPlugin>()

group = "io.briones.example"
version = "1.0-SNAPSHOT"

dependencies {
    val junitVersion = "5.6.2"
    val truthVersion = "1.0.1"

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    testImplementation("com.google.truth", "truth", truthVersion)
    testImplementation("com.google.truth.extensions", "truth-java8-extension", truthVersion)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// Register tasks for integration tests.
val integTest = "integTest"

sourceSets.create(integTest) {
    java.srcDir(file("src/integTest/java"))
    resources.srcDir(file("src/integTest/resources"))
    compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
    runtimeClasspath += output + compileClasspath
}

tasks.register<Test>(integTest) {
    description = "Run integration tests."
    group = "verification"
    testClassesDirs = sourceSets[integTest].output.classesDirs
    classpath = sourceSets[integTest].runtimeClasspath
    mustRunAfter(tasks["test"])
}

tasks.named("check") {
    dependsOn(integTest)
}
