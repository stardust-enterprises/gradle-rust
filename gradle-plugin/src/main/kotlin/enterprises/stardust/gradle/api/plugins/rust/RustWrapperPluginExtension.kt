package enterprises.stardust.gradle.api.plugins.rust

import enterprises.stardust.gradle.api.tasks.RustWrapperTask
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

/**
 * @author xtrm
 * @since 4.0.0
 */
interface RustWrapperPluginExtension {
    /**
     * The name of the `cargo` executable to use.
     *
     * This can be changed for an absolute path to a specific version of `cargo`, or for another cargo-compatible tool
     * like `cross`.
     *
     * By default, this is `"cargo"`.
     */
    val cargoExecutable: Property<String>

    /**
     * Whether to run `cargo` in release mode.
     *
     * By default, this is `false`.
     */
    val releaseMode: Property<Boolean>

    /**
     * Extra arguments to pass to `cargo` when running any command.
     *
     * This can be finer tuned by adding extra arguments to specific gradle-rust wrapper tasks instead.
     *
     * By default, this is empty.
     *
     * @see [enterprises.stardust.gradle.api.tasks.RustWrapperTask]
     */
    val extraArguments: ListProperty<String>

    /**
     * A container of targets to run tasks against.
     *
     * By default, this is empty.
     */
    val targets: NamedDomainObjectContainer<RustTarget>

    fun registerTask(task: String): Provider<RustWrapperTask>

    fun registerTask(task: String, config: Action<in RustWrapperTask>): Provider<RustWrapperTask>

    fun registerTask(task: String, config: Closure<in RustWrapperTask>): Provider<RustWrapperTask>

    fun registerTask(task: String, config: RustWrapperTask.() -> Unit): Provider<RustWrapperTask>
}