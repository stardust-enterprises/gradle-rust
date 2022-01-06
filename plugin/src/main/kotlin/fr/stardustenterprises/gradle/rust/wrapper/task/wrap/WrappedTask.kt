package fr.stardustenterprises.gradle.rust.wrapper.task.wrap

import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import org.gradle.process.internal.ExecException
import java.io.File

open class WrappedTask(
    val command: String
) : ConfigurableTask<WrapperExtension>() {

    override fun doTask() {
        executeCommand()
    }

    @Throws(ExecException::class)
    open fun executeCommand() {
        project.exec {
            it.commandLine(getCommandLine())
            it.args(getArguments())
            it.workingDir(getWorkingDir())
            it.environment(getEnvironment())
        }.assertNormalExitValue()
    }

    open fun getCommandLine(): String =
        configuration.command.getOrElse("cargo")

    open fun getArguments(): List<String> =
        listOf() //TODO

    open fun getWorkingDir(): File =
        configuration.crate.asFile.getOrElse(project.projectDir)

    open fun getEnvironment(): Map<String, String> =
        configuration.environment
}