package fr.stardustenterprises.gradle.common

import fr.stardustenterprises.gradle.common.ext.IExtension
import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.PluginTask
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class Plugin: Plugin<Project> {
    protected lateinit var project: Project
        private set

    abstract var pluginId: String

    protected val tasks = mutableListOf<PluginTask>()
    protected val extensions = mutableListOf<IExtension>()

    abstract fun setupTasks()
    abstract fun postProcess()

    override fun apply(target: Project) {
        this.project = target

        setupTasks()

        this.project.afterEvaluate {
            conflictsWithPlugins().firstOrNull(this.project.pluginManager::hasPlugin) ?: run {
                postProcess()
                return@afterEvaluate
            }
            val present = conflictsWithPlugins().filter(project.pluginManager::hasPlugin).toList()
            throw RuntimeException("Plugin $pluginId conflicts with the following plugins: $present")
        }
    }

    protected fun extension(extension: IExtension) {
        project.extensions.create("rust", WrapperExtension::class.java)
    }

    protected fun task(pluginTask: PluginTask) {
        this.tasks.add(pluginTask)
    }

    protected fun <T: IExtension> task(configurableTask: ConfigurableTask<T>, configureBlock: ConfigurableTask<T>.() -> Unit) {
        this.task(configurableTask)

    }

    open fun conflictsWithPlugins(): Array<String> = arrayOf()
}