package fr.stardustenterprises.gradle.rust.importer

import fr.stardustenterprises.gradle.common.Plugin
import fr.stardustenterprises.gradle.rust.importer.ext.ImporterExtension
import fr.stardustenterprises.gradle.rust.importer.task.FixJarTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.jvm.tasks.ProcessResources


class ImporterPlugin : Plugin() {
    override var pluginId = "fr.stardustenterprises.rust.importer"

    private lateinit var fixJarTaskProvider: TaskProvider<out FixJarTask>
    private lateinit var configuration: Configuration

    override fun applyPlugin() {
        this.configuration = project.configurations.create("rust")
        this.configuration.isCanBeConsumed = false
        this.configuration.isCanBeResolved = true

        val importerExtension = extension(ImporterExtension::class.java)
        this.fixJarTaskProvider = task(FixJarTask::class.java) { configure(importerExtension) }
    }

    @Suppress("UnstableApiUsage")
    override fun afterEvaluate(project: Project) {
        project.tasks.withType(ProcessResources::class.java)
            .named("processResources")
            .configure {
                it.from(configuration)
            }
        project.tasks.named("build").get().dependsOn(this.fixJarTaskProvider)
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.wrapper")
}