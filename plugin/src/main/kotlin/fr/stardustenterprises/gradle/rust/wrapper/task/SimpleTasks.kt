package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.task.wrap.WrappedTask

@Task(group = "rust", name = "test")
class TestTask : WrappedTask("test")

@Task(group = "rust", name = "run")
class RunTask : WrappedTask("run")