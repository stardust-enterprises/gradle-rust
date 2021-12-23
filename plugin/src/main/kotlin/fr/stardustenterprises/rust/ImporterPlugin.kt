package fr.stardustenterprises.rust

import fr.stardustenterprises.rust.common.RustPlugin

class ImporterPlugin : RustPlugin() {
    override var pluginId = "fr.stardustenterprises.rust.importer"

    override fun applyPlugin() {
        this.project.run {
            println("Applying Importer plugin...")
        }
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.wrapper")
}