package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension

@Task(
    group = "rust",
    name = "test"
)
class TestTask : ConfigurableTask<WrapperExtension>()  {
    override fun doTask() {
        TODO("Not yet implemented")
    }
}