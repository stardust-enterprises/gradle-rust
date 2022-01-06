package fr.stardustenterprises.gradle.common

import fr.stardustenterprises.gradle.common.ext.Extension
import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.PluginTask
import fr.stardustenterprises.gradle.common.task.Task
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class Plugin : Plugin<Project> {
    protected lateinit var project: Project
        private set

    abstract var pluginId: String

    private val postHooks = mutableListOf<Runnable>()

    open fun applyPlugin() = Unit
    open fun afterEvaluate() = Unit

    override fun apply(target: Project) {
        this.project = target

        applyPlugin()

        this.project.afterEvaluate {
            conflictsWithPlugins().firstOrNull(this.project.pluginManager::hasPlugin) ?: run {
                this.postHooks.forEach(Runnable::run)

                afterEvaluate()
                return@afterEvaluate
            }
            val present = conflictsWithPlugins().filter(project.pluginManager::hasPlugin).toList()
            throw RuntimeException("Plugin $pluginId conflicts with the following plugins: $present")
        }
    }

    protected fun <T> extension(extensionClass: Class<T>): T {
        val extensionAnnotation = extensionClass.getDeclaredAnnotation(Extension::class.java) ?: throw RuntimeException(
            "Extension class missing @Extension annotation!"
        )
        return this.project.extensions.create(extensionAnnotation.name, extensionClass)
    }

    protected fun <T : PluginTask> task(taskClass: Class<out T>): T {
        val taskAnnotation = taskClass.getDeclaredAnnotation(Task::class.java)
            ?: throw RuntimeException("Task class missing @Task annotation!")
        return this.project.tasks.create(taskAnnotation.name, taskClass).also {
            if (taskAnnotation.group != "NO-GROUP") {
                it.group = taskAnnotation.group
            }
        }
    }

    protected fun <T, C : ConfigurableTask<T>> task(
        configurableTask: Class<out C>, configureBlock: C.() -> Unit
    ) {
        val task = this.task(configurableTask)
        this.postHooks.add {
            configureBlock.invoke(task)
        }
    }

    open fun conflictsWithPlugins(): Array<String> = arrayOf()
}