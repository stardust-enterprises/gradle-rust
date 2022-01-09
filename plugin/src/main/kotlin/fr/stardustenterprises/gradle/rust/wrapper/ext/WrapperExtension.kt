@file:Suppress("UnstableApiUsage")

package fr.stardustenterprises.gradle.rust.wrapper.ext

import fr.stardustenterprises.gradle.common.ext.Extension
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.tomlj.Toml
import org.tomlj.TomlParseResult
import javax.inject.Inject


@Extension("rust")
abstract class WrapperExtension
@Inject constructor(
    project: Project
) {

    @Internal
    private val objects = project.objects

    @Input
    val command: Property<String> = objects.property(String::class.java).convention("cargo")

    @InputDirectory
    val crate: DirectoryProperty = objects.directoryProperty().convention(project.layout.projectDirectory)

    @Input
    val outputBaseName: Property<String> =
        objects.property(String::class.java).convention("") // default to the name in Cargo.toml

    @Input
    val targets: MutableMap<String, String> = mutableMapOf()

    @Input
    val compilerArgs: MutableMap<String, String> = mutableMapOf()

    @Input
    val environment: MutableMap<String, String> = mutableMapOf()

    fun getOutputBaseName(): String {
        return outputBaseName.getOrElse(getCargoName())
    }

    private fun getCargoName(): String {
        val cargoTomlFile = crate.file("Cargo.toml").get().asFile

        if (!cargoTomlFile.exists()) throw RuntimeException("Cargo.toml file not found!")

        val result: TomlParseResult = Toml.parse(cargoTomlFile.toPath())
        return result.getString("package.name")
            ?: throw RuntimeException("Couldn't find package name")
    }

    fun defaultTarget(
        binaryName: String = System.mapLibraryName(getOutputBaseName())
    ): Pair<String, String> {
        return Pair("", binaryName)
    }
}

