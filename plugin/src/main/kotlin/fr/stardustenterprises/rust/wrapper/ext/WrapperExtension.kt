package fr.stardustenterprises.rust.wrapper.ext

import fr.stardustenterprises.rust.common.ext.IConfigExtension
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class WrapperExtension
@Inject constructor(
    project: Project
) : IConfigExtension {

    private val objects = project.objects

    val cargoCommand: Property<String> = objects.property(String::class.java)
        .convention("cargo")

    val crate: DirectoryProperty = objects.directoryProperty()
        .convention(project.layout.buildDirectory)

    val targets: MutableMap<String, String> = mutableMapOf()

    val compilerArgs: MutableMap<String, String> = mutableMapOf()

    val environment: MutableMap<String, String> = mutableMapOf()

    fun default() = "" to ""
}

