package fr.stardustenterprises.gradle.rust.wrapper

import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.gradle.rust.wrapper.task.BuildTask
import fr.stardustenterprises.gradle.rust.wrapper.task.CleanTask
import fr.stardustenterprises.gradle.rust.wrapper.task.RunTask
import fr.stardustenterprises.gradle.rust.wrapper.task.TestTask
import fr.stardustenterprises.stargrad.StargradPlugin
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.TaskProvider
import java.io.ByteArrayOutputStream

class WrapperPlugin : StargradPlugin() {
    companion object {
        lateinit var targetContainer: NamedDomainObjectContainer<TargetOptions>
    }

    override val id = "fr.stardustenterprises.rust.wrapper"
    override val conflictsWith: Set<String> =
        setOf("fr.stardustenterprises.rust.importer", "java")

    private lateinit var wrapperExtension: WrapperExtension
    private lateinit var buildTaskProvider: TaskProvider<out BuildTask>

    override fun applyPlugin() {
        targetContainer = project.container(TargetOptions::class.java)

        project.configurations.create("default")
        wrapperExtension = registerExtension()

        this.buildTaskProvider = registerTask { configure(wrapperExtension) }
        registerTask<RunTask> { configure(wrapperExtension) }
        registerTask<TestTask> { configure(wrapperExtension) }
        registerTask<CleanTask> { configure(wrapperExtension) }
    }

    override fun afterEvaluate() {
        wrapperExtension.targets.forEach { target ->
            target.populateFrom(wrapperExtension)
        }
        project.artifacts.add("default", this.buildTaskProvider)

        if (!wrapperExtension.cargoInstallTargets.get()) {
            return
        }
        println("(gradle-rust/experimental) Target auto-install is enabled...")

        val rustupCommand = wrapperExtension.rustupCommand.get()

        val stdout = ByteArrayOutputStream()
        project.exec { exec ->
            exec.commandLine(rustupCommand)
            exec.args("target", "list", "--installed")
            exec.workingDir(wrapperExtension.crate.get().asFile)
            exec.environment(wrapperExtension.env)
            exec.standardOutput = stdout
        }.assertNormalExitValue()

        val installed = stdout.toString().split("\n")
            .toMutableList()
            .also { it.removeIf(String::isNullOrBlank) }

        wrapperExtension.targets.forEach { targetOptions ->
            if (installed.contains(targetOptions.target)) {
                return@forEach
            }
            println("Installing target \"${targetOptions.target}\" via rustup.")

            val command = targetOptions.command!!.lowercase()
            if (command.contains("cargo") &&
                !command.contains("cross")
            ) {
                project.exec { exec ->
                    exec.commandLine(rustupCommand)
                    exec.args("target", "add", targetOptions.target)
                    exec.workingDir(wrapperExtension.crate.get().asFile)
                    exec.environment(wrapperExtension.env)
                }.assertNormalExitValue()
            }
        }
    }
}
