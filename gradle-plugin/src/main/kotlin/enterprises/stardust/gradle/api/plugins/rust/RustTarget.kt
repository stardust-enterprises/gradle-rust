package enterprises.stardust.gradle.api.plugins.rust

import org.gradle.api.Named

data class RustTarget(
    private val name: String
): Named {
    override fun getName() = name
}