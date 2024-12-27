package enterprises.stardust.build

import org.gradle.api.*
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.tasks.JvmConstants
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.gradle.kotlin.dsl.provider.PrecompiledScriptPluginsSupport
import org.gradle.kotlin.dsl.provider.gradleKotlinDslJarsOf
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.assignment.plugin.gradle.AssignmentExtension
import org.jetbrains.kotlin.assignment.plugin.gradle.AssignmentSubplugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverGradleSubplugin

/**
 * This is an unfortunate workaround for Gradle not supporting accessing the `kotlin-dsl` plugin
 * from a composite buildscript.
 *
 * This replicates the behavior of the `kotlin-dsl` plugin as seen
 * [here](https://github.com/gradle/gradle/blob/93844251a39b463e0d03b2b2853ae3acbca8bf50/platforms/core-configuration/kotlin-dsl-plugins/src/main/kotlin/org/gradle/kotlin/dsl/plugins/dsl/KotlinDslPlugin.kt).
 *
 * @author xtrm
 */
class PoorMansKotlinDslPlugin : Plugin<Project> {
    private val kotlinModuleNames = arrayOf("stdlib", "reflect")
    private val kotlinArtifactConfigurationNames =
        arrayOf(JvmConstants.COMPILE_ONLY_CONFIGURATION_NAME, JvmConstants.TEST_IMPLEMENTATION_CONFIGURATION_NAME)

    override fun apply(target: Project): Unit = target.run {
        // Don't apply the Java Gradle Plugin plugin, it's handled in the custom convention scripts
        applyKotlinDslBasePlugin()
        applyPrecompiledScriptPlugins()
    }

    private fun Project.applyKotlinDslBasePlugin() {
        applyEmbeddedKotlinPlugin()
        applyKotlinDslCompilerPlugins()
        kotlinArtifactConfigurationNames.forEach { configuration ->
            dependencies.add(configuration, gradleKotlinDsl())
        }
    }

    private fun Project.applyEmbeddedKotlinPlugin() {
        plugins.apply(KotlinPluginWrapper::class.java)

        val embeddedKotlin = configurations.create("embeddedKotlin")
        kotlinModuleNames.forEach { module ->
            dependencies.add(embeddedKotlin.name, "org.jetbrains.kotlin:kotlin-$module")
        }
        kotlinArtifactConfigurationNames.forEach { configuration ->
            configurations.getByName(configuration).extendsFrom(embeddedKotlin)
        }
    }

    private fun Project.applyKotlinDslCompilerPlugins() {
        plugins.apply(SamWithReceiverGradleSubplugin::class.java)
        extensions.configure(SamWithReceiverExtension::class.java) {
            annotation(HasImplicitReceiver::class.qualifiedName!!)
        }

        plugins.apply(AssignmentSubplugin::class.java)
        extensions.configure(AssignmentExtension::class.java) {
            annotation(SupportsKotlinAssignmentOverloading::class.qualifiedName!!)
        }
    }

    private fun Project.applyPrecompiledScriptPlugins() {
        if (serviceOf<PrecompiledScriptPluginsSupport>().enableOn(Target(project))) {
            dependencies {
                "kotlinCompilerPluginClasspath"(gradleKotlinDslJarsOf(project))
                "kotlinCompilerPluginClasspath"(gradleApi())
            }
        }
    }

    private class Target(override val project: Project) : PrecompiledScriptPluginsSupport.Target {
        override val jvmTarget: Provider<JavaVersion> = project.provider { JavaVersion.current() }

        override val kotlinSourceDirectorySet: SourceDirectorySet
            get() = (project.extensions.getByName("sourceSets") as SourceSetContainer)["main"].extensions.getByName("kotlin") as SourceDirectorySet
    }
}