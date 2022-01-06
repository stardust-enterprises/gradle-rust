@file:Suppress("UnstableApiUsage")

package fr.stardustenterprises.gradle.rust.wrapper.ext

import fr.stardustenterprises.gradle.common.ext.Extension
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@Extension("rust")
abstract class WrapperExtension
@Inject constructor(
    project: Project
) {

    private val objects = project.objects

    val command: Property<String> = objects.property(String::class.java)
        .convention("cargo")

    val crate: DirectoryProperty = objects.directoryProperty()
        .convention(project.layout.projectDirectory)

    val outputBaseName: Property<String> = objects.property(String::class.java)
        .convention("") // default to the name in Cargo.toml

    val targets: MutableMap<String, String> = mutableMapOf()

    val compilerArgs: MutableMap<String, String> = mutableMapOf()

    val environment: MutableMap<String, String> = mutableMapOf()
}

