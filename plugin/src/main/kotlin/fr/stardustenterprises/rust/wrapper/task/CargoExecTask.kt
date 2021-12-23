package fr.stardustenterprises.rust.wrapper.task

import fr.stardustenterprises.rust.common.task.PluginTask
import fr.stardustenterprises.rust.wrapper.ext.WrapperExtension
import org.gradle.api.Project

class CargoExecTask: PluginTask<WrapperExtension>() {
    override fun doTask(project: Project) {
    }

    override fun configure(configuration: WrapperExtension) {
    }
}