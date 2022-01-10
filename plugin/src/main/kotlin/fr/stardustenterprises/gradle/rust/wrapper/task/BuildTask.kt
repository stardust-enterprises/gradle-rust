package fr.stardustenterprises.gradle.rust.wrapper.task

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import fr.stardustenterprises.gradle.common.task.ConfigurableTask
import fr.stardustenterprises.gradle.common.task.Task
import fr.stardustenterprises.gradle.rust.wrapper.ext.WrapperExtension
import org.apache.commons.io.FileUtils
import java.io.ByteArrayOutputStream
import java.io.File

@Task(
    group = "rust", name = "build"
)
open class BuildTask : ConfigurableTask<WrapperExtension>() {
    private val json = GsonBuilder()
        .generateNonExecutableJson()
        .setPrettyPrinting()
        .serializeNulls()
        .create()

    override fun doTask() {
        val outputFile = File(project.buildDir.absoluteFile, "rustOutput")
        FileUtils.deleteDirectory(outputFile)
        outputFile.mkdirs()

        val exportMap = mutableMapOf<String, File>()

        val cargoTomlFile = configuration.crate.file("Cargo.toml").get().asFile ?: throw RuntimeException("Cargo.toml file not found!")

        configuration.targets.forEach { target ->
            val args = mutableListOf("build", "--message-format=json")

            if (target.key.isNotEmpty()) args += "--target=${target.key}"

            val stdout = ByteArrayOutputStream()
            project.exec {
                it.commandLine(configuration.command.getOrElse("cargo"))
                it.args(args)
                it.workingDir(configuration.crate.asFile.getOrElse(project.projectDir))
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
                        if(manifestPath.equals(cargoTomlFile.absolutePath, true)) {
                            val array = jsonObject.getAsJsonArray("filenames")
                            if(array.size() > 1) {
                                throw RuntimeException("Cannot process more than 1 output.")
                            }
                            array.forEach {
                                val file = File(it.asString)
                                if(!file.exists()) {
                                    throw RuntimeException("Cannot find output file!")
                                }
                                output = file
                            }
                        }
                    }
                } catch(_: Throwable) {
                }
            }

            if(output == null) {
                throw RuntimeException("Didn't find the output file... report this.")
            }

            exportMap[target.key] = output!!
        }


    }
}
