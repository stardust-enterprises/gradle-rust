package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.gradle.rust.wrapper.task.wrap.WrappedTask
import org.apache.commons.io.FileUtils

@Task(group = "rust", name = "test")
open class TestTask : WrappedTask("test")

@Task(group = "rust", name = "run")
open class RunTask : WrappedTask("run")

@Task(group = "rust", name = "clean")
open class CleanTask : ConfigurableTask<WrapperExtension>() {
    override fun doTask() {
        val workingDir = this.configuration.crate.asFile.getOrElse(this.project.projectDir)

        FileUtils.deleteDirectory(workingDir.resolve("target"))
        FileUtils.deleteDirectory(this.project.projectDir.resolve("build"))
    }
}