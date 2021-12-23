package fr.stardustenterprises.rust.common

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class RustPlugin: Plugin<Project> {
    protected lateinit var project: Project
        private set

    open lateinit var pluginId: String

    override fun apply(target: Project) {
        this.project = target

        preSetup()
        this.project.afterEvaluate {
            conflictsWithPlugins().firstOrNull(this.project.pluginManager::hasPlugin) ?: run {
                applyPlugin()
                return@afterEvaluate
            }
            val present = conflictsWithPlugins().filter(project.pluginManager::hasPlugin).toList()
            throw RuntimeException("Plugin $pluginId conflicts with the following plugins: $present")
        }
    }

    open fun preSetup() = Unit

    abstract fun applyPlugin()

    open fun conflictsWithPlugins(): Array<String> = arrayOf()
}