package enterprises.stardust.gradle.api.plugins.rust

import enterprises.stardust.gradle.api.internal.tasks.rust.DefaultRustWrapperPluginExtension
import enterprises.stardust.gradle.api.tasks.RustWrapperTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JvmEcosystemPlugin
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import javax.inject.Inject

const val RUST_EXTENSION_NAME = "rust"

/**
 * @author xtrm
 * @since 4.0.0
 */
open class RustWrapperPlugin : Plugin<Project> {
    @get:Internal
    private val defaultRustTasks = arrayOf("build", "check", "test", "clean", "run")
    @get:Inject
    private lateinit var sourceSet: SourceSetContainer

    override fun apply(target: Project): Unit = target.run {
        logger.info("Hello from gradle-rust wrapper plugin!")

        if (plugins.hasPlugin(JvmEcosystemPlugin::class.java)) {
            throw IllegalStateException("The \"java\" or \"java-library\" plugin cannot be applied together with gradle-rust. " +
                "Consider moving your java-related code to a separate project.")
        }

        // First ensure the base plugin is applied
        apply<BasePlugin>()

        // Then create ourselves the `sourceSets` extension
        extensions.add(SourceSetContainer::class.java, "sourceSets", sourceSet)

        //TODO: Add our extensions for `rust` [enterprises.stardust.gradle.api.tasks.RustSourceDirectorySet]
        // see https://github.com/gradle/gradle/tree/master/platforms/jvm/scala/src/main/java/org/gradle/api/
        // as an example

        // Create the `rust` extension
        val rust = extensions.create(
            RustWrapperPluginExtension::class,
            RUST_EXTENSION_NAME,
            DefaultRustWrapperPluginExtension::class
        )

        // Create the default tasks
        defaultRustTasks.forEach { taskName ->
            rust.registerTask(taskName)
        }

        val testTask = tasks.register("rustTesting", RustWrapperTask::class) {
            group = "rust"
            description = "Testing testing testing"
        }
    }
}