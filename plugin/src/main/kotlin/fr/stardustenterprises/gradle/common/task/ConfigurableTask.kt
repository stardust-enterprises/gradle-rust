package fr.stardustenterprises.gradle.common.task

abstract class ConfigurableTask<in T> : PluginTask() {

    private var _configuration: @UnsafeVariance T? = null

    protected val configuration: @UnsafeVariance T
        get() = _configuration!!

    open fun applyConfiguration() = Unit

    fun configure(configuration: T) {
        this._configuration = configuration
    }

}