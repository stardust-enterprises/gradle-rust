package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension

@Task(
    group = "rust",
    name = "build"
)
open class BuildTask : ConfigurableTask<WrapperExtension>() {

    override fun doTask() {

    }

    private fun writeExports() {

    }
}
