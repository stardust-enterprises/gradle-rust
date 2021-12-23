private const val kotlinVersion = "1.6.10"

enum class Strategy {
    ROOT, SUBPROJECTS, ALL
}

data class Dependency(
    val id: String,
    val ver: String = ""
)

fun plugin(
    id: String,
    ver: String = "",
    applyStrategy: Strategy = Strategy.SUBPROJECTS
) = Pair(Dependency(id, ver), applyStrategy)

object Dependencies {
    val buildPlugins = listOf(
        plugin("org.jetbrains.kotlin.jvm", kotlinVersion),
        plugin("org.jlleitschuh.gradle.ktlint", "10.2.0"),
        plugin("org.jetbrains.dokka", "1.6.0"),
        plugin("com.gradle.plugin-publish", "0.18.0"),
        plugin("java-gradle-plugin"),
        plugin("maven-publish")
    )

    val libraries = listOf(
        Dependency("")
    )
}