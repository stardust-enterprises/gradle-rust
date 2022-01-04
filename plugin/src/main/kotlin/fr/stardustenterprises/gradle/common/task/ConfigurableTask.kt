package fr.stardustenterprises.gradle.common.task

import fr.stardustenterprises.gradle.common.ext.IExtension

abstract class ConfigurableTask<in T : IExtension> : PluginTask() {

    protected lateinit var configuration: @UnsafeVariance T
        private set

    open fun configure(configuration: T) = run {
        this.configuration = configuration
    }

}