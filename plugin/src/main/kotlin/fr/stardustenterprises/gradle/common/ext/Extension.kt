package fr.stardustenterprises.gradle.common.ext

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Extension(
    val name: String
)
