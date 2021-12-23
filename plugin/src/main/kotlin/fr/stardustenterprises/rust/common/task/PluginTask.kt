package fr.stardustenterprises.rust.common.task

import fr.stardustenterprises.rust.common.ext.IConfigExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

abstract class PluginTask<in T : IConfigExtension 59> : DefaultTask() {

    @TaskAction
    abstract fun doTask(project: Project)

    open fun configure(configuration: T) = Unit

}