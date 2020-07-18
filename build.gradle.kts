import com.github.spotbugs.snom.SpotBugsTask
import com.github.spotbugs.snom.Effort as SpotBugsEffort

import io.briones.gradle.PrettyPrintTestPlugin

plugins {
    java
    application
    checkstyle
    id("com.github.spotbugs").version("4.4.4")
}
apply<PrettyPrintTestPlugin>()

group = "io.briones.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val spotbugsVersion = spotbugs.toolVersion.get()
    val junitVersion = "5.6.2"

    // Application
    compileOnly(group = "com.github.spotbugs", name = "spotbugs-annotations", version = spotbugsVersion)

    // Tests + Static Analysis
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    checkstyle(group = "com.puppycrawl.tools", name = "checkstyle", version = "8.34")
    testImplementation(group = "org.assertj", name = "assertj-core", version = "3.16.1")

    // Annotation Processing
    compileOnly(group = "org.immutables", name = "value", version = "2.8.2")
    annotationProcessor(group = "org.immutables", name = "value", version = "2.8.2")
}

sourceSets {
    main {
        java.srcDirs.add(file("./build/generated/sources"))
    }
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

// Configure plugins

application {
    mainClassName = "io.briones.example.Main"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

spotbugs {
    showProgress.set(true)
    effort.set(SpotBugsEffort.MAX)
    ignoreFailures.set(false)
    excludeFilter.set(file("./config/spotbugs/exclude.xml"))
}

tasks.withType<SpotBugsTask>().configureEach {
    reports.create("html") {
        isEnabled = true
    }
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        html.isEnabled = true
    }
}

