package fr.stardustenterprises.gradle.common.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class PluginTask: DefaultTask() {

    abstract val taskId: String
    abstract val taskGroup: String

    @TaskAction
    abstract fun doTask()

}