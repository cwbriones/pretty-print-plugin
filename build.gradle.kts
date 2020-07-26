plugins {
    java
    `kotlin-dsl`
    `maven-publish`
}

group = "io.briones.gradle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

dependencies {
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        create("prettyPrintTestPlugin") {
            id = "io.briones.pretty-print"
            implementationClass = "io.briones.gradle.PrettyPrintTestPlugin"
        }
    }
}
