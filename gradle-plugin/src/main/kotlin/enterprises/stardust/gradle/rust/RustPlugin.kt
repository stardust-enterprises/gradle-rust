package enterprises.stardust.gradle.rust

import enterprises.stardust.gradle.common.AbstractDelegatingPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

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

            // Setup importing configurations
            val rust = configurations.register("rust")

            val cargoFile = layout.projectDirectory.file("Cargo.toml")
            if (cargoFile.asFile.exists()) {
                logger.info("Cargo.toml found, setting up rust plugin")
            }
        }
    }
}