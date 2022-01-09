package fr.stardustenterprises.gradle.rust.wrapper

import fr.stardustenterprises.gradle.common.Plugin
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.gradle.rust.wrapper.task.BuildTask
import fr.stardustenterprises.gradle.rust.wrapper.task.RunTask
import fr.stardustenterprises.gradle.rust.wrapper.task.TestTask

class WrapperPlugin : Plugin() {
    override var pluginId = "fr.stardustenterprises.rust.wrapper"
    private lateinit var wrapperExtension: WrapperExtension

    override fun applyPlugin() {
        wrapperExtension = extension(WrapperExtension::class.java)

        task(BuildTask::class.java) { configure(wrapperExtension) }
        task(RunTask::class.java) { configure(wrapperExtension) }
        task(TestTask::class.java) { configure(wrapperExtension) }
    }

    override fun afterEvaluate() {
        if (wrapperExtension.targets.isEmpty()) {
            throw RuntimeException("Please define a target platform.")
        }
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.importer", "java")
}
