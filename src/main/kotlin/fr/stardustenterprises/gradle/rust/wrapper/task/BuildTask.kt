package fr.stardustenterprises.gradle.rust.wrapper.task

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import fr.stardustenterprises.gradle.rust.data.Exports
import fr.stardustenterprises.gradle.rust.data.TargetExport
import fr.stardustenterprises.gradle.rust.wrapper.BouncerOutputStream
import fr.stardustenterprises.gradle.rust.wrapper.TargetManager
import fr.stardustenterprises.gradle.rust.wrapper.TargetOptions
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import fr.stardustenterprises.plat4k.EnumOperatingSystem
import fr.stardustenterprises.stargrad.task.ConfigurableTask
import fr.stardustenterprises.stargrad.task.Task
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.stream.Collectors
import java.util.zip.ZipException
import kotlin.io.path.Path
import kotlin.io.path.absolute

@Task(
    group = "rust", name = "build"
)
open class BuildTask : ConfigurableTask<WrapperExtension>() {
    companion object {
        private const val EXPORTS_FILE_NAME =
            "_fr_stardustenterprises_gradle_rust_exports.zip"

        private val json = GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()
    }

    private lateinit var workingDir: File

    @InputFiles
    lateinit var inputFiles: FileCollection
        private set

    @Input
    var targetsHash: Int = -1
        private set

    @OutputFile
    lateinit var exportsZip: File
        private set

    override fun applyConfiguration() {
        this.workingDir = this.configuration.crate.asFile.getOrElse(
            this.project.projectDir
        )

        this.inputFiles = project.files()

        this.workingDir.listFiles { f -> f.name.endsWith(".toml") }
            ?.forEach { this.inputFiles += this.project.fileTree(it) }
        this.inputFiles += this.project.fileTree(
            this.workingDir.resolve("src")
        )

        this.targetsHash = Objects.hash(
            *this.configuration.targets.toList().toTypedArray()
        )

        val rustDir = this.project.buildDir.resolve("rust")
        this.exportsZip = rustDir.resolve(EXPORTS_FILE_NAME)
    }

    override fun run() {
        if (configuration.targets.isEmpty()) {
            throw RuntimeException("Please define at least one target.")
        }

        TargetManager.ensureTargetsInstalled(project, configuration)

        val rustDir = this.project.buildDir.resolve("rust")
        FileUtils.deleteDirectory(rustDir)
        rustDir.mkdirs()

        val tmpDir = rustDir.resolve("temp").also(File::mkdirs)
        val exportMap = mutableMapOf<String, File>()

        val cargoTomlFile =
            this.configuration.crate.file("Cargo.toml").orNull?.asFile
                ?: throw RuntimeException("Cargo.toml file not found!")

        this.configuration.targets.forEach { targetOptions ->
            println(
                "Building \"%s\" for target \"%s\""
                    .format(targetOptions.name, targetOptions.target)
            )

            exportMap[targetOptions.target!!] =
                build(targetOptions, tmpDir, cargoTomlFile)
        }

        writeExports(exportMap)
        FileUtils.deleteDirectory(tmpDir)
    }

    private fun build(
        targetOpt: TargetOptions,
        tmpDir: File,
        cargoToml: File,
    ): File {
        val args = targetOpt.subcommand(
            "build", "--message-format=json"
        )

        val stdout = ByteArrayOutputStream()
        val target = targetOpt.target!!.lowercase()
        val command = targetOpt.command!!.lowercase()
        val currentOS = EnumOperatingSystem.currentOS

        val isOsxCross = target.contains("darwin") &&
            command.contains("cargo") &&
            currentOS != EnumOperatingSystem.MACOS

        try {
            this.project.exec {
                it.commandLine(targetOpt.command)
                it.args(args)
                it.workingDir(this.workingDir)
                it.environment(targetOpt.env)
                it.standardOutput = BouncerOutputStream(System.out, stdout)
                it.errorOutput = System.err
            }.assertNormalExitValue()
        } catch (throwable: Throwable) {
            if (isOsxCross) {
                println(
                    "[gradle-rust] Error cross-compiling to a darwin target (" +
                        targetOpt.target + ")."
                )
                println(
                    "[gradle-rust] Ensure your .cargo/config.toml file is " +
                        "configured properly with the osxcross toolchains."
                )
            }
        }

        var output: File? = null
        var error = false
        for (str in stdout.toString().trim().split("\n")) {
            try {
                val jsonStr = str.trim()

                val jsonObject = json.fromJson(jsonStr, JsonObject::class.java)
                val reason = jsonObject.get("reason").asString

                if (reason.equals("build-finished")) {
                    error = !jsonObject.get("success").asBoolean
                }
                if (reason.equals("compiler-message")) {
                    val message = jsonObject.get("message").asJsonObject.get("rendered").asString
                    println(message)
                }

                if (reason.equals("compiler-artifact")) {
                    var manifestPath = Path(jsonObject.get("manifest_path").asString)

                    if (manifestPath.startsWith("/project")) {
                        val subPath = manifestPath.subpath(1, manifestPath.nameCount)
                        val projectDir = Path(project.projectDir.absolutePath)
                        manifestPath = projectDir.resolve(subPath).absolute()
                    }

                    if (manifestPath == Path(cargoToml.absolutePath)) {
                        val elem = jsonObject.getAsJsonArray(
                            "filenames"
                        ).firstOrNull() { !it.isJsonNull }
                            ?: throw RuntimeException(
                                "Compiler did not give any valid output file"
                            )

                        var binPath = Path(elem.asString)
                        if (binPath.startsWith("/project")) {
                            val subPath = binPath.subpath(1, binPath.nameCount)
                            val projectDir = Path(project.projectDir.absolutePath)
                            binPath = projectDir.resolve(subPath).absolute()
                        }

                        var file = File(binPath.toString())
                        if (!file.exists()) {
                            file = File(project.projectDir, binPath.toString())
                            if (!file.exists()) {
                                throw RuntimeException(
                                    "Cannot find output file!"
                                )
                            }
                        }
                        output = file
                        break
                    }
                }
            } catch (_: Throwable) {
            }
        }

        if (error) {
            throw RuntimeException(
                "An error occured during the compilation process, see logs above. \nCommand: " +
                    targetOpt.command + " " + args.joinToString(" ")
            )
        }

        if (output == null) {
            throw RuntimeException(
                "Didn't find the output file... report this.\nCommand: " +
                    targetOpt.command + " " + args.joinToString(" ")
            )
        }

        println("Output: " + output.absolutePath)

        val newOut = tmpDir.resolve(targetOpt.target!!)
            .also(File::mkdirs)
            .resolve(targetOpt.outputName!!)

        if (!newOut.exists()) {
            newOut.createNewFile()
        }
        output.copyTo(newOut, overwrite = true)

        System.out.flush()
        System.err.flush()

        return newOut
    }

    private fun writeExports(map: Map<String, File>) {
        val rustDir = this.project.buildDir.resolve("rust")

        val outputDir = rustDir.resolve("outputs")
        outputDir.mkdirs()

        val exportsFile = rustDir.resolve("exports.json")

        val exportsList = mutableListOf<TargetExport>()

        val skipTags = arrayOf(
            "unknown", "pc", "sun", "nvidia", "gnu",
            "msvc", "none", "elf", "wasi", "uwp",
        ) // does eabi/abi(64) belong in there?

        val skipSecondTags = arrayOf("apple", "linux")

        map.forEach {
            var parsed = it.key.split('-').toMutableList()

            if (skipSecondTags.contains(parsed[1])) {
                parsed.removeAt(1)
            }

            var arch = parsed[0]
            parsed.removeAt(0)

            parsed = parsed.map { data ->
                var newData = data
                skipTags.forEach { skip ->
                    newData = newData.replace(skip, "")
                }
                newData
            }.toMutableList()
            parsed.filter(String::isEmpty).forEach(parsed::remove)

            parsed = parsed.map { data ->
                var newData = data
                if (newData.endsWith("hf") ||
                    newData.contains("hardfloat")
                ) {
                    newData = newData.replace("hf", "")
                        .replace("hardfloat", "")
                    arch += "hf"
                }
                if (newData.endsWith("sf") ||
                    newData.contains("softfloat")
                ) {
                    newData = newData.replace("sf", "")
                        .replace("softfloat", "")
                    arch += "sf"
                }
                newData
            }.toMutableList()
            parsed.filter(String::isEmpty).forEach(parsed::remove)

            var os = parsed.stream().collect(Collectors.joining("-"))
            if (os.isEmpty()) os = "unknown"

            exportsList += TargetExport(os, arch, it.key, it.value.name)

            val targetDir = outputDir.resolve(it.key)
            targetDir.mkdirs()
            val targetFile = targetDir.resolve(it.value.name)
            if (targetFile.exists()) targetFile.delete()

            Files.copy(it.value.toPath(), targetFile.toPath())
        }

        val exports = Exports(1, exportsList)
        exportsFile.writeText(json.toJson(exports))

        val files: Array<File> = outputDir.listFiles()
            ?: throw RuntimeException("No outputs >.>")

        if (this.exportsZip.exists()) {
            this.exportsZip.delete()
        }
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
