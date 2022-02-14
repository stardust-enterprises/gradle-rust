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
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@Extension("rust")
abstract class WrapperExtension
@Inject constructor(
    val project: Project
) {
    @Internal
    private val objects = project.objects

    @Input
    val command: Property<String> = objects.property(String::class.java).convention("cargo")

    @InputDirectory
    val crate: DirectoryProperty = objects.directoryProperty().convention(project.layout.projectDirectory)

    @Input
    val outputBaseName: Property<String> =
        objects.property(String::class.java).convention(getCargoName()) // default to the name in Cargo.toml

    @Input
    val release: Boolean = false

    @Input
    val targets: MutableMap<String, String> = mutableMapOf()

    @Input
    val compilerArgs: MutableMap<String, String> = mutableMapOf()

    @Input
    val environment: MutableMap<String, String> = mutableMapOf()

    private fun getCargoName(): String {
        val cargoTomlFile =
            crate.file("Cargo.toml").get().asFile ?: throw RuntimeException("Cargo.toml file not found!")
        if (!cargoTomlFile.exists()) throw RuntimeException("Cargo.toml file not found!")

        val result = Toml.parse(cargoTomlFile.toPath())
        return result.getString("package.name") ?: throw RuntimeException("Couldn't find package name")
    }

    private val defaultTarget: String by lazy {
        val stdout = ByteArrayOutputStream()
        project.exec {
            it.commandLine("rustup")
            it.args("default")
            it.workingDir(crate.asFile.getOrElse(project.projectDir))
            it.environment(environment)
            it.standardOutput = stdout
        }.assertNormalExitValue()

        val targetOutput = stdout.toString()
            .replace("(default)", "")
            .replace("stable", "")
            .replace('\n', ' ').trim()

        targetOutput.split('-').filter(String::isNotEmpty).joinToString("-")
    }

    fun defaultTarget(
        binaryName: String = System.mapLibraryName(outputBaseName.get())
    ): Pair<String, String> = Pair(defaultTarget, binaryName)
}

