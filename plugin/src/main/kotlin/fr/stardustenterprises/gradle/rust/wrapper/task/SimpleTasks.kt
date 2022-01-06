package fr.stardustenterprises.gradle.rust.wrapper.task

import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.task.wrap.WrappedTask

@Task(group = "rust", name = "test")
class TestTask : WrappedTask(TestTask::class.java.getDeclaredAnnotation(Task::class.java).name)

@Task(group = "rust", name = "run")
class RunTask : WrappedTask(RunTask::class.java.getDeclaredAnnotation(Task::class.java).name)