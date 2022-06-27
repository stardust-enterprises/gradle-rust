private const val kotlinVersion = "1.7.0"

object Plugins {
    const val KOTLIN = kotlinVersion
    const val GRGIT = "4.1.1" // old version for jgit to work on Java 8
    const val KTLINT = "10.3.0"
    const val DOKKA = kotlinVersion
    const val GRADLE_PLUGIN_PUBLISH = "0.21.0"
}

object Dependencies {
    const val KOTLIN = kotlinVersion
    const val STARGRAD = "0.5.2"
    const val PLAT4K = "1.6.0"
    const val TOMLJ = "1.0.0"
    const val COMMONS_IO = "2.11.0"
    const val GSON = "2.8.9"
    const val ZIP4J = "2.9.1"

    val kotlinModules = arrayOf("stdlib")
}

object Repositories {
    val mavenUrls = arrayOf(
        "https://jitpack.io/",
    )
}
