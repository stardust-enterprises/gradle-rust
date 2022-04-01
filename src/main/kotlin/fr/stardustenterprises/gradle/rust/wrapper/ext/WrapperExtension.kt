@file:Suppress("UnstableApiUsage")

package fr.stardustenterprises.gradle.rust.wrapper.ext

import fr.stardustenterprises.stargrad.ext.Extension
import fr.stardustenterprises.stargrad.ext.StargradExtension
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.tomlj.Toml
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@Extension("rust")
abstract class WrapperExtension
@Inject constructor(
    _project: Project,
) : StargradExtension(_project) {
    @Input
    val command: Property<String> = objects.property(String::class.java)
        .convention("cargo")

    @Input
    val toolchain: Property<String> = objects.property(String::class.java)
        .convention("")

    @InputDirectory
    val crate: DirectoryProperty = objects.directoryProperty()
        .convention(project.layout.projectDirectory)

    @Input
    val outputBaseName: Property<String> = objects.property(String::class.java)
        .convention(getCargoName()) // default to the name in Cargo.toml

    @Input
    val release: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(false)

    @Input
    val targets: MutableMap<String, String> = mutableMapOf()

    @Input
    val compilerArgs: MutableSet<String> = mutableSetOf()

    @Input
    val environment: MutableMap<String, String> = mutableMapOf()

    private fun getCargoName(): String {
        val cargoTomlFile =
            crate.file("Cargo.toml").orNull?.asFile ?: throw RuntimeException("Cargo.toml file not found!")
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

        var targetOutput = stdout.toString()
            .replace("(default)", "")
            .replace('\n', ' ').trim()

        // stable-, naughty-
        targetOutput = targetOutput.substring(
            targetOutput.indexOf('-') + 1
        )

        targetOutput.split('-').filter(String::isNotEmpty).joinToString("-")
    }

    fun defaultTarget(
        binaryName: String = System.mapLibraryName(outputBaseName.get()),
    ): Pair<String, String> = Pair(defaultTarget, binaryName)
}
