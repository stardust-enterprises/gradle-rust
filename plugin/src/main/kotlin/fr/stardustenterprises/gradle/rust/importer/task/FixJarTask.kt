package fr.stardustenterprises.gradle.rust.importer.task

import com.google.gson.GsonBuilder
import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.data.Exports
import fr.stardustenterprises.gradle.rust.importer.LAYOUT_REGISTRY
import fr.stardustenterprises.gradle.rust.importer.ext.ImporterExtension
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import java.io.File
import java.io.FileReader

@Task(group = "rustImport", name = "extract")
open class FixJarTask : ConfigurableTask<ImporterExtension>() {
    companion object {
        private const val EXPORTS_FILE_NAME =
            "_fr_stardustenterprises_gradle_rust_exports.zip"

        private val json = GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()
    }

    @Internal
    val outputPaths: MutableList<File> = mutableListOf()

    override fun doTask() {
        val layout = LAYOUT_REGISTRY[configuration.layout.get()]
            ?: throw RuntimeException("Invalid layout. " +
                    "(${LAYOUT_REGISTRY.keys})")

        val dir = project.buildDir.resolve("rustImport")
        FileUtils.deleteDirectory(dir)
        dir.mkdirs()

        val jarFile = (project.tasks.getByName("jar")
                as AbstractArchiveTask).archiveFile.get().asFile

        val extractDir = dir.resolve("extract").also(File::mkdirs)

        ZipFile(jarFile).extractAll(extractDir.absolutePath)

        val exportsZip = extractDir.resolve(EXPORTS_FILE_NAME).also {
            if (!it.exists()) throw RuntimeException("Exports zip not found!")
        }

        val exportsDir = dir.resolve("exports").also(File::mkdirs)
        ZipFile(exportsZip).extractAll(exportsDir.absolutePath)

        val exportsFile = exportsDir.resolve("exports.json").also {
            if (!it.exists()) throw RuntimeException("Exports json not found!")
        }

        val root = configuration.baseDir.get()
            .replace('/', File.separatorChar)

        val fileReader = FileReader(exportsFile)
        val exports = json.fromJson(fileReader, Exports::class.java)
        exports.targets.forEach { target ->
            val name = target.binaryFile

            val binFile = exportsDir.resolve(target.targetTriple).resolve(name)

            val newPath = layout.getPathForTarget(
                root,
                target.targetOperatingSystem,
                target.targetArchitecture,
                name
            ).replace('/', File.separatorChar).let {
                if (it.startsWith(File.separatorChar)) {
                    return@let it.substring(1)
                }
                it
            }

            val newBin = extractDir.resolve(
                newPath
            ).also { f -> if (f.exists()) f.delete() }

            binFile.copyTo(newBin, overwrite = true)
        }

        exportsZip.delete()

        jarFile.delete()
        val finalZip = ZipFile(jarFile)

        extractDir.listFiles()!!.forEach { file ->
            if (file.isDirectory) {
                finalZip.addFolder(file)
            } else {
                finalZip.addFile(file)
            }
        }
    }
}
