package fr.stardustenterprises.gradle.rust.importer

import fr.stardustenterprises.gradle.common.Plugin

class ImporterPlugin : Plugin() {
    override var pluginId = "fr.stardustenterprises.rust.importer"

    override fun postProcess() {
        this.project.run {
            println("Applying Importer plugin...")
        }
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.wrapper")
}