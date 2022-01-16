package fr.stardustenterprises.gradle.rust.data

import java.io.File

data class Exports(
    val exportsVersion: Int,
    val targets: List<TargetExport> = listOf()
)

data class TargetExport(
    val targetTriple: String,
    val targetOperatingSystem: String,
    val targetArchitecture: String,
    val binaryFile: File
)