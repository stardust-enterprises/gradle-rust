package fr.stardustenterprises.gradle.rust.wrapper

import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import org.gradle.api.Project
import java.io.ByteArrayOutputStream

object TargetManager {
    fun ensureTargetsInstalled(
        project: Project,
        wrapperExtension: WrapperExtension,
    ) {
        if (wrapperExtension.cargoInstallTargets.get()) {
            installTargets(project, wrapperExtension)
        }
    }

    private fun installTargets(
        project: Project,
        wrapperExtension: WrapperExtension,
    ) {
        println("(gradle-rust/experimental) Target auto-install is enabled.")
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
