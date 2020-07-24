plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("gradle.plugin.com.github.spotbugs.snom", "spotbugs-gradle-plugin", "4.4.4")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
