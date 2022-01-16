package fr.stardustenterprises.gradle.rust.wrapper.task

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.data.Exports
import fr.stardustenterprises.gradle.rust.data.TargetExport
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.stream.Collectors
import java.util.zip.ZipException

@Task(
    group = "rust", name = "build"
)
open class BuildTask : ConfigurableTask<WrapperExtension>() {
    companion object {
        private val json = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    }

    @InputDirectory
    lateinit var workingDir: File

    @Input
    lateinit var targets: Set<String>

    @OutputFile
    lateinit var exportsZip: File
        private set

    override fun applyConfiguration() {
        this.workingDir = configuration.crate.asFile.getOrElse(project.projectDir)
        this.targets = configuration.targets.keys

        val rustDir = project.buildDir.resolve("rust")
        this.exportsZip = rustDir.resolve("exports.zip")
    }

    override fun doTask() {
        val exportMap = mutableMapOf<String, File>()

        val cargoTomlFile =
            configuration.crate.file("Cargo.toml").get().asFile ?: throw RuntimeException("Cargo.toml file not found!")

        configuration.targets.forEach { target ->
            val args = mutableListOf("build", "--message-format=json")

            if (target.key.isNotEmpty()) args += "--target=${target.key}"

            val stdout = ByteArrayOutputStream()
            project.exec {
                it.commandLine(configuration.command.getOrElse("cargo"))
                it.args(args)
                it.workingDir(workingDir)
                it.environment(configuration.environment)
                it.standardOutput = stdout
            }.assertNormalExitValue()

            var output: File? = null

            for (str in stdout.toString().trim().split("\n")) {
                try {
                    val jsonStr = str.trim()
                    val jsonObject = json.fromJson(jsonStr, JsonObject::class.java)
                    val reason = jsonObject.get("reason").asString
                    if (reason.equals("compiler-artifact", true)) {
                        val manifestPath = jsonObject.get("manifest_path").asString
                        if (manifestPath.equals(cargoTomlFile.absolutePath, true)) {
                            val array = jsonObject.getAsJsonArray("filenames")
                            if (array.size() > 1) {
                                throw RuntimeException("Cannot process more than 1 output.")
                            }
                            array.forEach {
                                val file = File(it.asString)
                                if (!file.exists()) {
                                    throw RuntimeException("Cannot find output file!")
                                }
                                output = file
                            }
                        }
                    }
                } catch (_: Throwable) {
                }
            }

            if (output == null) {
                throw RuntimeException("Didn't find the output file... report this.")
            }

            exportMap[target.key] = output!!
        }

        writeExports(exportMap)
    }

    private fun writeExports(map: Map<String, File>) {
        val rustDir = project.buildDir.resolve("rust")
        FileUtils.deleteDirectory(rustDir)
        rustDir.mkdirs()
        val outputDir = rustDir.resolve("outputs")
        outputDir.mkdirs()

        val exportsFile = rustDir.resolve("exports.json")

        val exportsList = mutableListOf<TargetExport>()

        val skipTags = arrayOf(
            "unknown", "pc", "sun", "nvidia", "gnu", "msvc", "none", "elf", "wasi", "uwp"
        ) // does eabi/abi(64) belong in there?

        val skipSecondTags = arrayOf("apple", "linux")

        map.forEach {
            var parsedData = it.key.split('-').toMutableList()

            if (skipSecondTags.contains(parsedData[1])) {
                parsedData.removeAt(1)
            }

            var arch = parsedData[0]
            parsedData.removeAt(0)

            parsedData = parsedData.map { data ->
                var newData = data
                skipTags.forEach { skip ->
                    newData = newData.replace(skip, "")
                }
                newData
            }.toMutableList()
            parsedData.filter(String::isEmpty).forEach(parsedData::remove)

            parsedData = parsedData.map { data ->
                var newData = data
                if (newData.endsWith("hf") || newData.contains("hardfloat")) {
                    newData = newData.replace("hf", "").replace("hardfloat", "")
                    arch += "hf"
                }
                if (newData.endsWith("sf") || newData.contains("softfloat")) {
                    newData = newData.replace("sf", "").replace("softfloat", "")
                    arch += "sf"
                }
                newData
            }.toMutableList()
            parsedData.filter(String::isEmpty).forEach(parsedData::remove)

            var os = parsedData.stream().collect(Collectors.joining("-"))
            if (os.isEmpty()) os = "unknown"

            exportsList += TargetExport(os, arch, it.key, it.value.name)

            val targetDir = outputDir.resolve(it.key)
            targetDir.mkdirs()
            val targetFile = targetDir.resolve(it.value.name)
            if (targetFile.exists()) targetFile.delete()
            it.value.copyTo(targetFile)
        }

        val exports = Exports(1, exportsList)
        exportsFile.writeText(json.toJson(exports))

        val files: Array<File> = outputDir.listFiles() ?: throw RuntimeException("No outputs >.>")

        if (exportsZip.exists()) exportsZip.delete()
        val zipFile = ZipFile(this.exportsZip)

        try {
            zipFile.addFile(exportsFile)
        } catch (e: ZipException) {
            throw RuntimeException(e)
        }

        for (file in files) {
            try {
                if (file.isFile) {
                    zipFile.addFile(file)
                } else {
                    zipFile.addFolder(file)
                }
            } catch (e: ZipException) {
                throw RuntimeException(e)
            }
        }
    }
}