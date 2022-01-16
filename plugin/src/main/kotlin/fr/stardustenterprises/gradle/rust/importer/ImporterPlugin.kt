package fr.stardustenterprises.gradle.rust.importer

import fr.stardustenterprises.gradle.common.Plugin
import org.gradle.api.Project

class ImporterPlugin : Plugin() {
    override var pluginId = "fr.stardustenterprises.rust.importer"

    override fun afterEvaluate(project: Project) {
        this.project.run {
            println("Applying Importer plugin...")
        }
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.wrapper")
}