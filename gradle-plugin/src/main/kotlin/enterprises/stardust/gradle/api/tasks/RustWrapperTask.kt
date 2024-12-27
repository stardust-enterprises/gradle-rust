package enterprises.stardust.gradle.api.tasks

import enterprises.stardust.gradle.api.plugins.rust.RustWrapperPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * @author xtrm
 * @since 4.0.0
 */
open class RustWrapperTask : DefaultTask() {
    @get:Inject
    private lateinit var rustExt: RustWrapperPluginExtension

    @TaskAction
    fun run() {
        println("Hello from gradle-rust wrapper task!")
        println(rustExt.cargoExecutable.get())
    }
}