package fr.stardustenterprises.gradle.rust.data

data class Exports(
    val exportsVersion: Int,
    val targets: List<TargetExport> = listOf()
)

data class TargetExport(
    val targetOperatingSystem: String,
    val targetArchitecture: String,
    val targetTriple: String,
    val binaryFile: String
)
