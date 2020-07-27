plugins {
    java
    `kotlin-dsl`
    `maven-publish`

    id("io.gitlab.arturbosch.detekt") version "1.10.0"
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
