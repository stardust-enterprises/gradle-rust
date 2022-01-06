package fr.stardustenterprises.gradle.rust.wrapper

import fr.stardustenterprises.gradle.common.Plugin
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.gradle.rust.wrapper.task.BuildTask
import fr.stardustenterprises.gradle.rust.wrapper.task.RunTask
import fr.stardustenterprises.gradle.rust.wrapper.task.TestTask

class WrapperPlugin : Plugin() {
    override var pluginId = "fr.stardustenterprises.rust.wrapper"

    override fun applyPlugin() {
        val wrapperExt = extension(WrapperExtension::class.java)
        task(BuildTask::class.java) { configure(wrapperExt) }
        task(RunTask::class.java) { configure(wrapperExt) }
        task(TestTask::class.java) { configure(wrapperExt) }
    }

    override fun conflictsWithPlugins(): Array<String> =
        arrayOf("fr.stardustenterprises.rust.importer", "java")
}
