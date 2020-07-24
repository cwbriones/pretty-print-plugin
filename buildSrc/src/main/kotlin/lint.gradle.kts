import com.github.spotbugs.snom.SpotBugsTask
import com.github.spotbugs.snom.Effort as SpotBugsEffort

plugins {
    java
    pmd
    checkstyle
    id("com.github.spotbugs")
}

pmd {
    toolVersion = "6.23.0"

    ruleSetFiles(
        "config/pmd/ruleset.xml"
    )

    ruleSets(
        "category/java/errorprone.xml",
        "category/java/bestpractices.xml"
    )
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

checkstyle {
    toolVersion = "8.34"
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        html.isEnabled = true
    }
}

dependencies {
    val spotbugsVersion = spotbugs.toolVersion.get()
    compileOnly(group = "com.github.spotbugs", name = "spotbugs-annotations", version = spotbugsVersion)
}
