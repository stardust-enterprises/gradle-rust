package fr.stardustenterprises.gradle.rust.wrapper.task.wrap

import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.stargrad.task.ConfigurableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.process.internal.ExecException
import java.io.File

open class WrappedTask(
    @Internal
    protected val command: String
) : ConfigurableTask<WrapperExtension>() {

    override fun run() {
        configuration.targets.forEach {
            val args = getArguments().toMutableList()
            args.add(0, command)
            if (it.key.isNotEmpty()) args += "--target=${it.key}"
            executeCommand(arguments = args)
        }
    }

    @Throws(ExecException::class)
    open fun executeCommand(
        commandLine: String = getCommandLine(),
        arguments: List<String> = getArguments(),
        workingDir: File = getWorkingDir(),
        environment: Map<String, String> = getEnvironment()
    ) {
        project.exec {
            it.commandLine(commandLine)
            it.args(arguments)
            it.workingDir(workingDir)
            it.environment(environment)
        }.assertNormalExitValue()
    }

    @Input
    open fun getCommandLine(): String = configuration.command.getOrElse("cargo")

    @Input
    open fun getArguments(): List<String> = listOf()

    @InputDirectory
    open fun getWorkingDir(): File = configuration.crate.asFile.getOrElse(project.projectDir)

    @Input
    open fun getEnvironment(): Map<String, String> = configuration.environment
}
