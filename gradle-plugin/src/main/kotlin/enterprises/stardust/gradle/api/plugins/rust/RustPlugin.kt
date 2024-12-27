package enterprises.stardust.gradle.api.plugins.rust

import enterprises.stardust.gradle.api.plugins.AbstractDelegatingPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * @author xtrm
 * @since 4.0.0
 */
open class RustPlugin : AbstractDelegatingPlugin(
    projectPlugin = ProjectRustPlugin::class
) {
    /**
     * @author xtrm
     * @since 4.0.0
     */
    open class ProjectRustPlugin : Plugin<Project> {
        override fun apply(target: Project) = target.run {
            logger.info("Hello from gradle-rust 4.0.0!")

            setupImportingConfigurations()

            val cargoFile = layout.projectDirectory.file("Cargo.toml")
            if (cargoFile.asFile.exists()) {
                logger.info("Cargo.toml found, setting up rust plugin")
                apply<RustWrapperPlugin>()
            }
        }

        private fun Project.setupImportingConfigurations() {
//            val rust = configurations.register("rust")

            //TODO: ideally something like
//             dependencies {
//                 implementation(rust(project(":rust-lib))!!)
//             }

            //TODO: maybe uhhhh
//            dependencies {
//                implementation(rust(project(":rust-lib")) {
//                    target = "META-INF/natives"
//                })
//            }
        }
    }
}