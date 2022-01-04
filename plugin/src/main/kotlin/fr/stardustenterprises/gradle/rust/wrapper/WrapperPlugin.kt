package fr.stardustenterprises.gradle.rust.wrapper

import fr.stardustenterprises.gradle.common.Plugin
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.gradle.rust.wrapper.task.BuildTask
import fr.stardustenterprises.gradle.rust.wrapper.task.RunTask
import fr.stardustenterprises.gradle.rust.wrapper.task.TestTask

class WrapperPlugin : Plugin() {
    private lateinit var wrapperExtension: WrapperExtension
    private lateinit var buildTask: BuildTask
    private lateinit var runTask: RunTask
    private lateinit var testTask: TestTask

    override var pluginId = "fr.stardustenterprises.rust.wrapper"

    override fun setupTasks() {
        wrapperExtension = project.extensions.create("rust", WrapperExtension::class.java)
        buildTask = project.tasks.create("build", BuildTask::class.java).apply {
            this.group = "rust"
        }
    }

    override fun postProcess() {
        buildTask.configure(wrapperExtension)
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.importer", "java")
}
