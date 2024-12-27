package enterprises.stardust.gradle.api.plugins

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.PluginAware
import kotlin.reflect.KClass

/**
 * @author xtrm
 * @since 0.0.1
 */
abstract class AbstractDelegatingPlugin(
    gradlePlugin: KClass<out Plugin<Gradle>>? = null,
    settingsPlugin: KClass<out Plugin<Settings>>? = null,
    projectPlugin: KClass<out Plugin<Project>>? = null,
    extraPlugins: Map<Class<out PluginAware>, KClass<*>> = emptyMap()
) : Plugin<PluginAware> {
    private val pluginsMap = buildMap<Class<out PluginAware>, KClass<*>> {
        projectPlugin?.let { put(Project::class.java, it) }
        gradlePlugin?.let { put(Gradle::class.java, it) }
        settingsPlugin?.let { put(Settings::class.java, it) }
        extraPlugins.forEach { (key, value) -> put(key, value) }
    }

    override fun apply(target: PluginAware) {
        val plugin = pluginsMap[target.javaClass]
            ?: throw GradleException("Target type '${target.javaClass.simpleName}' is not supported by this plugin.")
        target.pluginManager.apply(plugin.java)
    }
}