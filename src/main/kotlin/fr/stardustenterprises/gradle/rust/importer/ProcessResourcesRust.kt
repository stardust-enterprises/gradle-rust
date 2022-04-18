package fr.stardustenterprises.gradle.rust.importer

import com.google.gson.GsonBuilder
import fr.stardustenterprises.gradle.rust.data.Exports
import fr.stardustenterprises.gradle.rust.importer.ext.ImporterExtension
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileReader

object ProcessResourcesRust {
    private const val EXPORTS_FILE_NAME =
        "_fr_stardustenterprises_gradle_rust_exports.zip"

    private val json = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create()

    val outputPaths: MutableList<File> = mutableListOf()

    fun process(
        project: Project,
        configuration: ImporterExtension,
        baseDir: File,
    ) {
        val layout = LAYOUT_REGISTRY[configuration.layout.get()]
            ?: throw RuntimeException(
                "Invalid layout. (" +
                    LAYOUT_REGISTRY.keys +
                    ")"
            )

        val rustImportDir = project.buildDir.resolve("rustImport")
        FileUtils.deleteDirectory(rustImportDir)
        rustImportDir.mkdirs()

        val exportsZip = baseDir.resolve(EXPORTS_FILE_NAME).also {
            if (!it.exists()) throw RuntimeException("Exports zip not found!")
        }

        val exportsDir = rustImportDir
            .resolve("exports")
            .also(File::mkdirs)

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

            val newBin = baseDir.resolve(
                newPath
            ).also { f -> if (f.exists()) f.delete() }

            binFile.copyTo(newBin, overwrite = true)
        }

        exportsZip.delete()
    }
}
