package fr.stardustenterprises.gradle.common.task

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Task(
    val group: String = "NO-GROUP",
    val name: String
)
