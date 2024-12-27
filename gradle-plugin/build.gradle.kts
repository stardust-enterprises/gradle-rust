plugins {
    id("enterprises.stardust.build.gradle-plugin")
}

version = "4.0.0-indev"
description = "Rust support for Gradle"

dependencies {
    include(implementation(projects.stardustGradleCommons)!!)
}

publishing {
    publications {
        all {
            this as MavenPublication
            artifactId = "gradle-rust"
        }
    }
}