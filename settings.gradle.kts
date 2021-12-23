pluginManagement.repositories {
    mavenLocal()
    gradlePluginPortal()
}

rootProject.name = "gradle-rust"
include("plugin")

include("example:library")
include("example:program")
