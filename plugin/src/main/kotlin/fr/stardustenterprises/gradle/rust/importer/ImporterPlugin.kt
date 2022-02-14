package fr.stardustenterprises.gradle.rust.importer

import fr.stardustenterprises.gradle.common.Plugin
import fr.stardustenterprises.gradle.rust.importer.ext.ImporterExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.language.jvm.tasks.ProcessResources


class ImporterPlugin : Plugin() {
    override var pluginId = "fr.stardustenterprises.rust.importer"

    private lateinit var configuration: Configuration
    private lateinit var importerExtension: ImporterExtension

    override fun applyPlugin() {
        this.configuration = project.configurations.create("rust")
        this.configuration.isCanBeConsumed = false
        this.configuration.isCanBeResolved = true

        this.importerExtension = extension(ImporterExtension::class.java)
    }

    @Suppress("UnstableApiUsage")
    override fun afterEvaluate(project: Project) {
        project.tasks.withType(ProcessResources::class.java)
            .named("processResources")
            .also {
                it.configure { t ->
                    t.from(configuration)
                }
            }.get().doLast {
                val baseDir = (it as ProcessResources).destinationDir
                ProcessResourcesRust.process(project, importerExtension, baseDir)
            }
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.wrapper")
}