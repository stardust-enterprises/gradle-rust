plugins {
    kotlin("jvm") version "1.6.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("org.jetbrains.dokka") version "1.6.0"
}

repositories {
    mavenCentral()
}

subprojects {
    group = Coordinates.GROUP
    version = Coordinates.VERSION

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        mavenCentral()
    }
}
