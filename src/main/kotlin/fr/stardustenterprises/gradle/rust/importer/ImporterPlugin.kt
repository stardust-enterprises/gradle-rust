package fr.stardustenterprises.gradle.rust.importer

import fr.stardustenterprises.gradle.rust.importer.ext.ImporterExtension
import fr.stardustenterprises.stargrad.StargradPlugin
import org.gradle.api.artifacts.Configuration
import org.gradle.language.jvm.tasks.ProcessResources

class ImporterPlugin : StargradPlugin() {
    override val id = "fr.stardustenterprises.rust.importer"
    override val conflictsWith: Set<String> =
        setOf("fr.stardustenterprises.rust.wrapper")

    private lateinit var configuration: Configuration
    private lateinit var importerExtension: ImporterExtension

    override fun applyPlugin() {
        this.configuration = project.configurations.create("rust")
        this.configuration.isCanBeConsumed = false
        this.configuration.isCanBeResolved = true

        this.importerExtension = registerExtension()
    }

    @Suppress("UnstableApiUsage")
    override fun afterEvaluate() {
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
}
