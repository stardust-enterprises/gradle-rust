package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.task.wrap.WrappedTask

@Task(group = "rust", name = "test")
open class TestTask : WrappedTask("test")

@Task(group = "rust", name = "run")
open class RunTask : WrappedTask("run")