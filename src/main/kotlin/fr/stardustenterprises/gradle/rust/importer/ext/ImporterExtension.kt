@file:Suppress("UnstableApiUsage")

package fr.stardustenterprises.gradle.rust.importer.ext

import fr.stardustenterprises.stargrad.ext.Extension
import fr.stardustenterprises.stargrad.ext.StargradExtension
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

@Extension("rustImport")
abstract class ImporterExtension
@Inject constructor(
    _project: Project
): StargradExtension(_project) {
    @Input
    val baseDir: Property<String> = objects.property(String::class.java)
        .convention("/META-INF/natives/")

    @Input
    val layout: Property<String> = objects.property(String::class.java)
        .convention("hierarchical")
}
