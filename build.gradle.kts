plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("lint")
    id("testing")
}

group = "io.briones.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClassName = "io.briones.example.Main"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(group = "org.slf4j", name = "slf4j-api", version = "2.0.0-alpha1")
    runtimeOnly(group = "ch.qos.logback", name = "logback-core", version = "1.3.0-alpha5")
    runtimeOnly(group = "ch.qos.logback", name = "logback-classic", version = "1.3.0-alpha5")
    compileOnly(group = "org.immutables", name = "value", version = "2.8.2")
    annotationProcessor(group = "org.immutables", name = "value", version = "2.8.2")
}

sourceSets {
    main {
        java.srcDirs.add(file("./build/generated/sources"))
    }
}

tasks.withType<Tar>().configureEach {
    compression = Compression.GZIP
}
