package fr.stardustenterprises.gradle.rust.wrapper

import fr.stardustenterprises.gradle.common.StardustPlugin
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.gradle.rust.wrapper.task.BuildTask
import fr.stardustenterprises.gradle.rust.wrapper.task.CleanTask
import fr.stardustenterprises.gradle.rust.wrapper.task.RunTask
import fr.stardustenterprises.gradle.rust.wrapper.task.TestTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

class WrapperPlugin : StardustPlugin() {
    override val pluginId = "fr.stardustenterprises.rust.wrapper"
    private lateinit var wrapperExtension: WrapperExtension
    private lateinit var buildTaskProvider: TaskProvider<out BuildTask>

    override fun applyPlugin() {
        project.configurations.create("default")
        wrapperExtension = extension(WrapperExtension::class.java)

        this.buildTaskProvider = task(BuildTask::class.java) { configure(wrapperExtension) }
        task(RunTask::class.java) { configure(wrapperExtension) }
        task(TestTask::class.java) { configure(wrapperExtension) }
        task(CleanTask::class.java) { configure(wrapperExtension) }
    }

    override fun afterEvaluate(project: Project) {
        if (wrapperExtension.targets.isEmpty()) {
            throw RuntimeException("Please define a target platform.")
        }

        project.artifacts.add("default", this.buildTaskProvider)
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.importer", "java")
}
