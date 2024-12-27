// This is needed to prevent Gradle from loading the Kotlin Gradle plugin twice in subprojects.
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.sam.with.receiver) apply false
    alias(libs.plugins.kotlin.assignment) apply false
}