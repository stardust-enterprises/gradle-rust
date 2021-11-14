// Copyright 2021 Arc'blroth
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package fr.stardustenterprises.rust.wrapper;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * The main wrapper task.
 */
public class WrapperTask extends DefaultTask {

    private static final String OUTPUT_FILE_NAME = "rustOutput";

    private String command;
    private List<String> args;
    private Map<String, String> environment;

    private String outputDirectory;

    private String profile;

    private Map<String, String> targets;

    /**
     * This is an imperfect solution to incremental builds:
     * if a change is made to the source code, this directory
     * will change, and Gradle will run Cross. But once the
     * first execution runs, the directory will change, and
     * Gradle will need to invoke Cross a second time even if
     * no rebuild is necessary. Luckily, Cross itself is pretty
     * fast at determining whether a rebuild is needed,
     * and will not modify this directory if no rebuild is
     * performed, thus allowing this task to be skipped if it
     * is invoked a third time.
     */
    private File workingDir;

    private File outputFile;

    /**
     * <b>For internal use only.</b>
     * Configures this task with the given options.
     *
     * @param config Extension object to fetch options from.
     */
    protected void configure(WrapperExtension config) {
        Project project = getProject();

        if (config.command != null && config.command.isEmpty()) {
            throw new GradleException("Cross command cannot be empty");
        }
        this.command = config.command == null ? "cargo" : config.command;

        this.args = new ArrayList<>();

        if (config.toolchain != null) {
            // Remove a preceding `+`, if present.
            String toolchain = config.toolchain.startsWith("+") ? config.toolchain.substring(1) : config.toolchain;
            if (toolchain.isEmpty()) {
                throw new GradleException("Toolchain cannot be empty");
            }
            this.args.add("+" + toolchain);
        }

        this.args.add("build");
        if (!"debug".equals(config.profile)) {
            this.args.add("--release");
        }

        this.outputDirectory = config.outputDirectory;
        this.profile = config.profile;

        this.args.addAll(config.arguments);

        this.environment = new ConcurrentHashMap<>(config.environment);

        this.workingDir = config.crate != null ? project.file(config.crate) : project.getProjectDir();

        this.targets = new HashMap<>(config.outputs);

        this.outputFile = new File(project.getBuildDir(), OUTPUT_FILE_NAME + File.separator + "export.zip");
        File parent = this.outputFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("Couldn't create output folder!");
        }

        if (this.targets.isEmpty()) {
            throw new GradleException("At least one output must be specified.");
        }
    }

    /**
     * Builds the specified crate using Cross.
     *
     * @throws org.gradle.process.internal.ExecException If execution fails.
     */
    @TaskAction
    public void build() {
        Project project = getProject();

        File outputStore = new File(project.getBuildDir(), OUTPUT_FILE_NAME);
        if (outputStore.exists()) {
            try {
                FileUtils.deleteDirectory(outputStore);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        outputStore.mkdirs();

        // build targets
        boolean useCrossSyntax = this.command.toLowerCase(Locale.ROOT).contains("cross");
        if (useCrossSyntax) {
            if (this.targets == null || this.targets.isEmpty()) {
                project.exec(spec -> {
                    System.out.println("Building for default target...");

                    spec.commandLine(this.command);
                    spec.args(args);
                    spec.workingDir(workingDir);
                    spec.environment(environment);
                }).assertNormalExitValue();
            } else {

                this.targets.forEach((target, name) -> {
                    System.out.println("Building for \"" + target + "\" target... (" + name + ")");

                    List<String> targetArgs = new ArrayList<>(this.args);
                    targetArgs.add("--target");
                    targetArgs.add(target);

                    project.exec(spec -> {
                        spec.commandLine(this.command);
                        spec.args(targetArgs);
                        spec.workingDir(workingDir);
                        spec.environment(this.environment);
                    }).assertNormalExitValue();
                });
            }
        } else {
            System.out.println("Building for default target...");

            project.exec(spec -> {
                spec.commandLine(this.command);
                spec.args(args);
                spec.workingDir(workingDir);
                spec.environment(environment);
            }).assertNormalExitValue();
        }

        // compress into archive
        if (this.outputFile.exists() && !this.outputFile.delete()) {
            throw new RuntimeException("Couldn't delete old output file.");
        }

        // move files
        global:
        for (String targetTriple : this.targets.keySet()) {
            File targetBuildDir = new File(
                    this.workingDir,
                    "target" + File.separator +
                            (targetTriple.isEmpty() ? "" : targetTriple + File.separator) +
                            this.profile + File.separator);

            if (!targetBuildDir.exists() || targetBuildDir.listFiles() == null) {
                throw new RuntimeException("Invalid target directory for \"" + targetTriple + "\"!");
            }

            String outputName = this.targets.get(targetTriple);
            int extIndex = outputName.lastIndexOf('.');
            String extension = extIndex == -1
                    ? "" : outputName.substring(extIndex + 1);

            String[] targetData = targetTriple.split(Pattern.quote("-"));
            String arch = targetData[0];
            // account for weird targets like aarch64-fuchsia
            String osName = targetData[targetData.length > 2 ? 2 : 1];

            String pathToOutput = this.outputDirectory
                    + File.separator + osName
                    + File.separator + arch
                    + File.separator + outputName;

            for (File potentialOutput : requireNonNull(targetBuildDir.listFiles())) {
                if (!potentialOutput.isFile()) continue;

                String name = potentialOutput.getName();
                String extension2 = name.lastIndexOf('.') == -1
                        ? "" : name.substring(name.lastIndexOf('.') + 1);

                if (extension.equalsIgnoreCase(extension2)) {
                    File targetFile = new File(outputStore, pathToOutput);
                    targetFile.getParentFile().mkdirs();

                    if (targetFile.exists() && !targetFile.delete()) {
                        throw new RuntimeException("Couldn't delete output file!");
                    }

                    try {
                        Files.copy(potentialOutput.toPath(), targetFile.toPath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    continue global;
                }
            }
        }

        File[] files = outputStore.listFiles();
        if (files == null) {
            throw new RuntimeException("No outputs >.>");
        }

        ZipFile zipFile = new ZipFile(this.outputFile);
        for (File file : files) {
            try {
                if (file.isFile()) {
                    zipFile.addFile(file);
                } else {
                    zipFile.addFolder(file);
                }
            } catch (ZipException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @return The working directory of this task.
     */
    @InputDirectory
    public File getWorkingDir() {
        return this.workingDir;
    }

    @OutputFiles
    public List<File> getOutputFile() {
        return Collections.singletonList(this.outputFile);
    }
}
