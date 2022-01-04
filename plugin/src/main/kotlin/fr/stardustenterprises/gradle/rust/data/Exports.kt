package fr.stardustenterprises.gradle.rust.data

import java.io.File

data class Exports(
    val rustPluginVersion: String,
    val targets: Map<String, File> = mutableMapOf(),
)