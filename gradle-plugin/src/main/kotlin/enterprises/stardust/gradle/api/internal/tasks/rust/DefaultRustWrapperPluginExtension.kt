package enterprises.stardust.gradle.api.internal.tasks.rust

import enterprises.stardust.gradle.api.plugins.rust.RustTarget
import enterprises.stardust.gradle.api.plugins.rust.RustWrapperPluginExtension
import enterprises.stardust.gradle.api.tasks.RustWrapperTask
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskContainer
import javax.inject.Inject

/**
 * @author xtrm
 * @since 4.0.0
 */
open class DefaultRustWrapperPluginExtension
@Inject constructor(
    objectFactory: ObjectFactory,
    val taskContainer: TaskContainer,
) : RustWrapperPluginExtension {
    override val cargoExecutable: Property<String> =
        objectFactory.property(String::class.java).value("cargo")
    override val releaseMode: Property<Boolean> =
        objectFactory.property(Boolean::class.java).value(false)
    override val extraArguments: ListProperty<String> =
        objectFactory.listProperty(String::class.java).empty()
    override val targets: NamedDomainObjectContainer<RustTarget> =
        objectFactory.domainObjectContainer(RustTarget::class.java)

    override fun registerTask(task: String): Provider<RustWrapperTask> =
        this.registerTask(task) {}

    override fun registerTask(
        task: String,
        config: Action<in RustWrapperTask>
    ): Provider<RustWrapperTask> =
        this.registerTask(task) { config.execute(this) }

    override fun registerTask(
        task: String,
        config: Closure<in RustWrapperTask>
    ): Provider<RustWrapperTask> =
        this.registerTask(task) {
            config.delegate = this
            config.call()
        }

    override fun registerTask(
        task: String,
        config: RustWrapperTask.() -> Unit
    ): Provider<RustWrapperTask> =
        taskContainer.register(task, RustWrapperTask::class.java) {
            group = "rust"
            config(this)
        }
}