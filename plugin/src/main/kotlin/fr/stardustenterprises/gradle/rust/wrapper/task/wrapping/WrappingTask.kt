package fr.stardustenterprises.gradle.rust.wrapper.task.wrapping

import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import org.gradle.process.internal.ExecException

open class WrappingTask(
    val command: String
) : ConfigurableTask<WrapperExtension>() {

    override fun doTask() {
        TODO("Not yet implemented")
    }

    @Throws(ExecException::class)
    open fun executeCommand() {
        val workingDir = configuration.crate.asFile.getOrElse(project.projectDir)

        project.exec() {
            it.commandLine()
            it.args()
            it.workingDir(workingDir)
            it.environment(configuration.environment)
        }.assertNormalExitValue()
    }

}