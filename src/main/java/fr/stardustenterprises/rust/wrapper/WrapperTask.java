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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The main wrapper task.
 */
public class WrapperTask extends DefaultTask {

    private String command;
    private List<String> args;
    private Map<String, String> environment;

    private List<String> targets;

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

    private List<File> outputFiles;

    private File outputArchive;

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

        this.targets = new ArrayList<>();

        config.outputs.entrySet().stream()
                .map((Function<Map.Entry<String, String>, Object>) Map.Entry::getKey)
                .filter(Objects::nonNull)
                .map(String.class::cast)
                .forEach(this.targets::add);

        // For the default toolchain, the output is located in target/<file>
        // For all other toolchains, the output is located in target/<target-triple>/<file>.
        this.outputFiles = config.outputs.entrySet().stream()
                .map(output ->
                        new File(targetDir,
                                (output.getKey().isEmpty() ?
                                        "" :
                                        output.getKey() + File.separator
                                )
                                        + config.profile
                                        + File.separator +
                                        output.getValue()
                        )
                ).collect(Collectors.toList());
        if (this.outputFiles.isEmpty()) {
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
        // compress into archive
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
        return outputArchive;
    }
}
