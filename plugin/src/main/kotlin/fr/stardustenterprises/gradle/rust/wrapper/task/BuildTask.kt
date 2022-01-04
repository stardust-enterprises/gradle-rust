package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension

open class BuildTask: ConfigurableTask<WrapperExtension>() {
    override val taskId = "build"
    override val taskGroup = "rust"

    override fun doTask() {

    }

    private fun writeExports() {

    }
}
