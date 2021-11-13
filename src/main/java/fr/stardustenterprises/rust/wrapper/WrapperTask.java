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

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The main wrapper task.
 */
public class WrapperTask extends DefaultTask {

    private static final String OUTPUT_FILE_NAME = "rustOutput";

    private String command;
    private List<String> args;
    private Map<String, String> environment;

    private Map<String, File> targets;

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

        this.args.addAll(config.arguments);

        this.environment = new ConcurrentHashMap<>(config.environment);

        this.workingDir = config.crate != null ? project.file(config.crate) : project.getProjectDir();
        File targetDir = new File(this.workingDir, "target");

        this.targets = new HashMap<>();

        // For the default toolchain, the output is located in target/<file>
        // For all other toolchains, the output is located in target/<target-triple>/<file>.
        config.outputs.forEach((k, v) -> {
            File parent = new File(targetDir, (k.isEmpty() ? "" : k + File.separator) + config.profile);
            this.targets.put(k, new File(parent, v));
        });

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

        File outputStore = new File(project.getBuildDir(), OUTPUT_FILE_NAME + File.separator + "root");
        if (!outputStore.exists() && !outputStore.mkdirs()) {
            throw new RuntimeException("Couldn't create output store.");
        }

        // normalize names
        List<File> outputFiles = new ArrayList<>();
        label:
        for (File expectedOutput : this.targets.values()) {
            if (!expectedOutput.exists()) {
                File parent = expectedOutput.getParentFile();
                File[] subFiles = parent.listFiles();

                if (subFiles == null) {
                    throw new RuntimeException("What the fuck");
                }

                String name = expectedOutput.getName();

                int index = name.lastIndexOf('.');
                if (index == -1) {
                    for (File f : subFiles) {
                        if (f.isFile()) {
                            if (f.getName().lastIndexOf('.') == -1) {
                                outputFiles.add(defineCorrectFilename(outputStore, name, f));
                                continue label;
                            }
                        }
                    }
                } else {
                    String ext = name.substring(index + 1);
                    for (File f : subFiles) {
                        if (f.isFile()) {
                            int index2 = f.getName().lastIndexOf('.');
                            if (index2 != -1 && f.getName().substring(index2 + 1).equalsIgnoreCase(ext)) {
                                outputFiles.add(defineCorrectFilename(outputStore, name, f));
                                continue label;
                            }
                        }
                    }
                }
                continue;
            }
            outputFiles.add(expectedOutput);
        }

        outputFiles.forEach(f -> System.out.println(f.exists() + f.getAbsolutePath()));

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(this.outputFile.toPath()))) {
            for (File file : outputFiles) {
                Path path = file.toPath();
                ZipEntry zipEntry = new ZipEntry(path.getFileName().toString());
                zipOutputStream.putNextEntry(zipEntry);
                Files.copy(path, zipOutputStream);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Couldn't write output file", exception);
        }
    }

    private File defineCorrectFilename(File outputStore, String name, File f) {
        File newFile = new File(outputStore, name);
        if (newFile.exists()) newFile.delete();
        try (FileInputStream fis = new FileInputStream(f);
             FileOutputStream fos = new FileOutputStream(newFile)) {
            int len;
            byte[] buffer = new byte[4096];
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException ignored) {
        }
        return newFile;
    }

    /**
     * @return The working directory of this task.
     */
    @InputDirectory
    public File getWorkingDir() {
        return this.workingDir;
    }

    @OutputFile
    public File getOutputFile() {
        return this.outputFile;
    }
}
