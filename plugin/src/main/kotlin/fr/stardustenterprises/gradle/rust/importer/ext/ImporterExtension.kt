@file:Suppress("UnstableApiUsage")

package fr.stardustenterprises.gradle.rust.importer.ext

import fr.stardustenterprises.gradle.common.ext.Extension
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import javax.inject.Inject

@Extension("rustImport")
abstract class ImporterExtension
@Inject constructor(
    project: Project
) {
    @Internal
    private val objects = project.objects

    @Input
    val baseDir: Property<String> = objects.property(String::class.java)
        .convention("/META-INF/natives/")

    @Input
    val layout: Property<String> = objects.property(String::class.java)
        .convention("hierarchical")
}