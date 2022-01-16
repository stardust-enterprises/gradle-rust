package fr.stardustenterprises.gradle.rust.importer

import fr.stardustenterprises.gradle.rust.data.TargetExport

val layouts = mapOf(
    "flat" to "{name}",
    "hierarchical" to "{os}/{arch}/{name}"
)

fun getLayout(name: String, target: TargetExport): String {
    val layout =
        layouts[name] ?: throw RuntimeException("Unknown layout, valid ones are: ${layouts.keys.joinToString(", ")}")
    return layout
        .replace("{os}", target.targetOperatingSystem)
        .replace("{arch}", target.targetArchitecture)
        .replace("{name}", target.binaryFile)
}