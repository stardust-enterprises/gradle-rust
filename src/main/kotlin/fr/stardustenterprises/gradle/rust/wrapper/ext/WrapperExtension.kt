package fr.stardustenterprises.gradle.rust.wrapper.ext

import fr.stardustenterprises.gradle.rust.wrapper.TargetOptions
import fr.stardustenterprises.gradle.rust.wrapper.WrapperPlugin
import fr.stardustenterprises.stargrad.ext.Extension
import fr.stardustenterprises.stargrad.ext.StargradExtension
import org.gradle.api.NamedDomainObjectContainer
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

    @InputDirectory
    val crate: DirectoryProperty = objects.directoryProperty()
        .convention(project.layout.projectDirectory)

    @Input
    val targets: NamedDomainObjectContainer<TargetOptions> =
        WrapperPlugin.targetContainer

    // Global Properties

    @Input
    val command: Property<String> = objects.property(String::class.java)
        .convention("cargo")

    @Input
    val toolchain: Property<String> = objects.property(String::class.java)
        .convention("")

    @Input
    val release: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(false)

    @Input
    val args: MutableList<String> =
        mutableListOf()

    @Input
    val env: MutableMap<String, String> =
        mutableMapOf()

    // Helper functions

    private val cargoName: String by lazy {
        val cargoTomlFile = crate.file("Cargo.toml").orNull?.asFile
            ?: throw RuntimeException("Cargo.toml file not found!")

        if (!cargoTomlFile.exists()) {
            throw RuntimeException("Cargo.toml file not found!")
        }

        val result = Toml.parse(cargoTomlFile.toPath())
        result.getString("package.name")
            ?: throw RuntimeException("Couldn't find cargo package name")
    }

    private val _defaultTarget: TargetOptions by lazy {
        val stdout = ByteArrayOutputStream()
        project.exec {
            it.commandLine("rustup")
            it.args("default")
            it.workingDir(crate.asFile.getOrElse(project.projectDir))
            it.environment(emptyMap<String, Any>())
            it.standardOutput = stdout
        }.assertNormalExitValue()

        var targetOutput = stdout.toString()
            .replace("(default)", "")
            .replace('\n', ' ').trim()

        // stable-, naughty-
        if (targetOutput.startsWith("stable-")
            || targetOutput.startsWith("naughty-")
            || targetOutput.startsWith("beta-")
        ) {
            targetOutput = targetOutput.substring(
                targetOutput.indexOfFirst { it == '-' } + 1
            )
        }

        // remove trailing dashes
        targetOutput = targetOutput.split('-')
            .filter(String::isNotEmpty)
            .joinToString("-")

        TargetOptions(
            "default",
            outputName = System.mapLibraryName(cargoName),
            target = targetOutput
        )
    }

    fun defaultTarget(): TargetOptions =
        _defaultTarget
}
