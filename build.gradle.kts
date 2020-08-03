plugins {
    java
    `kotlin-dsl`
    `maven-publish`

    id("io.gitlab.arturbosch.detekt") version "1.10.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
}

group = "io.briones.gradle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter {
        content {
            // detekt needs 'kotlinx-html' for the html report
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

dependencies {
    implementation(gradleApi())
    implementation(group = "com.diogonunes", name = "JColor", version = "5.0.0")

    val truthVersion = "1.0.1"
    val junitVersion = "5.6.2"

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine", version = junitVersion)
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-params", version = junitVersion)
    testImplementation(group = "com.google.truth", name = "truth", version = truthVersion)
    testImplementation(group = "com.google.truth.extensions", name = "truth-java8-extension", version = truthVersion)
}

publishing {
    repositories {
        mavenLocal()
    }
}

gradlePlugin {
    plugins {
        create("prettyPrintTestPlugin") {
            id = "io.briones.pretty-print"
            implementationClass = "io.briones.gradle.PrettyPrintTestPlugin"
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
