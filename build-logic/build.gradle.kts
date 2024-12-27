plugins {
    `kotlin-dsl`
}

description = "Convention plugins and build scripts for gradle-rust."

repositories {
    mavenCentral()
    gradlePluginPortal()
}

fun gradlePlugin(id: String, version: String? = null): String =
    "$id:$id.gradle.plugin${version?.let { ":$it" }}"

dependencies {
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())

    // Dependency on the Kotlin Gradle plugin is a special case, see root build.gradle.kts
    compileOnly(gradlePlugin("org.jetbrains.kotlin.jvm", "2.1.0"))
    compileOnly(kotlin("sam-with-receiver"))
    compileOnly(kotlin("assignment"))

    // Transitive Gradle plugin dependencies
    implementation(libs.reproducible.builds)
    implementation(libs.gradle.plugin.publish)
}