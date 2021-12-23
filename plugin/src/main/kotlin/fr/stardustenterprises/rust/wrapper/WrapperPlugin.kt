package fr.stardustenterprises.rust.wrapper

import fr.stardustenterprises.rust.common.RustPlugin
import fr.stardustenterprises.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.rust.wrapper.task.CargoExecTask

class WrapperPlugin : RustPlugin() {
    override var pluginId: String = "fr.stardustenterprises.rust.wrapper"

    override fun applyPlugin() {
        val wrapperExtension = project.extensions.create("rust", WrapperExtension::class.java)

        this.project.afterEvaluate {
            it.run {
                tasks.create("", CargoExecTask::class.java).run {
                    this.group = "rust"
                    this.configure(wrapperExtension)
                }
            }
        }
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.importer")
}