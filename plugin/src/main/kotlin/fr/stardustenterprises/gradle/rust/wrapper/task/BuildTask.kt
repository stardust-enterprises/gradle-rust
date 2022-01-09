package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.task.wrap.WrappedTask

@Task(
    group = "rust",
    name = "build"
)
open class BuildTask : WrappedTask("build") {

    override fun doTask() {

    }

    private fun writeExports() {

    }
}
