package fr.stardustenterprises.gradle.rust.importer

import java.io.File

val LAYOUT_REGISTRY = mutableMapOf(
    "hierarchical" to HierarchicalLayout(),
    "flat" to FlatLayout()
)

@FunctionalInterface
interface ILayout {
    fun getPathForTarget(
        root: String,
        osName: String,
        archName: String,
        targetName: String,
    ): String
}

class HierarchicalLayout : ILayout {
    override fun getPathForTarget(
        root: String,
        osName: String,
        archName: String,
        targetName: String,
    ): String =
        root +
                (if (root.endsWith(File.separator)) "" else File.separator) +
                targetName
}

class FlatLayout : ILayout {
    override fun getPathForTarget(
        root: String,
        osName: String,
        archName: String,
        targetName: String,
    ): String =
        root +
                (if (root.endsWith(File.separator)) "" else File.separator) +
                osName +
                File.separator +
                (if (archName.isNotEmpty()) {
                    archName +
                            File.separator
                } else "") +
                targetName

}