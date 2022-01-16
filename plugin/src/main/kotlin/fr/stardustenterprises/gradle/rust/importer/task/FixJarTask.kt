package fr.stardustenterprises.gradle.rust.importer.task

import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.importer.ext.ImporterExtension
import org.gradle.api.tasks.Internal
import java.io.File

@Task(group = "rustImport", name = "extract")
open class FixJarTask: ConfigurableTask<ImporterExtension>() {

    @Internal
    val outputPaths: MutableList<File> = mutableListOf()

    override fun applyConfiguration() {
        val baseDir = configuration.baseDir.get()

    }

    override fun doTask() {
        // extract all from jar to A
        // extract archive to TMP
        // for all exports, lay out files in A
        // copy all A into jar
        // gaming
    }
}