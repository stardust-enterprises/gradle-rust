package fr.stardustenterprises.gradle.rust.wrapper

import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.gradle.rust.wrapper.task.BuildTask
import fr.stardustenterprises.gradle.rust.wrapper.task.CleanTask
import fr.stardustenterprises.gradle.rust.wrapper.task.RunTask
import fr.stardustenterprises.gradle.rust.wrapper.task.TestTask
import fr.stardustenterprises.stargrad.StargradPlugin
import org.gradle.api.tasks.TaskProvider

class WrapperPlugin : StargradPlugin() {
    override val id = "fr.stardustenterprises.rust.wrapper"
    override val conflictsWith: Set<String> =
        setOf("fr.stardustenterprises.rust.importer", "java")

    private lateinit var wrapperExtension: WrapperExtension
    private lateinit var buildTaskProvider: TaskProvider<out BuildTask>

    override fun applyPlugin() {
        project.configurations.create("default")
        wrapperExtension = registerExtension()

        this.buildTaskProvider = registerTask { configure(wrapperExtension) }
        registerTask<RunTask> { configure(wrapperExtension) }
        registerTask<TestTask> { configure(wrapperExtension) }
        registerTask<CleanTask> { configure(wrapperExtension) }
    }

    override fun afterEvaluate() {
        if (wrapperExtension.targets.isEmpty()) {
            throw RuntimeException("Please define a target platform.")
        }

        project.artifacts.add("default", this.buildTaskProvider)
    }
}
