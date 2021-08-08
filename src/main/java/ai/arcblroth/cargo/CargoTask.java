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

package ai.arcblroth.cargo;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The main Cargo wrapper task.
 */
public class CargoTask extends DefaultTask {

    private String cargoCommand;
    private List<String> args;
    private Map<String, String> environment;

    /**
     * This is an imperfect solution to incremental builds:
     * if a change is made to the source code, this directory
     * will change, and Gradle will run Cargo. But once the
     * first execution runs, the directory will change, and
     * Gradle will need to invoke Cargo a second time even if
     * no rebuild is necessary. Luckily, Cargo itself is pretty
     * fast at determining whether a rebuild is needed,
     * and will not modify this directory if no rebuild is
     * performed, thus allowing this task to be skipped if it
     * is invoked a third time.
     */
    private File workingDir;

    private List<File> outputFiles;

    /**
     * <b>For internal use only.</b>
     * Configures this task with the given options.
     *
     * @param config Extension object to fetch options from.
     */
    protected void configure(CargoExtension config) {
        Project project = getProject();

        if (config.cargoCommand != null && config.cargoCommand.isEmpty()) {
            throw new GradleException("Cargo command cannot be empty");
        }
        this.cargoCommand = config.cargoCommand == null ? "cargo" : config.cargoCommand;

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

        // For the default toolchain, the output is located in target/<file>
        // For all other toolchains, the output is located in target/<target-triple>/<file>.
        this.outputFiles = config.outputs.entrySet().stream().map(output ->
                new File(targetDir, (output.getKey().isEmpty() ? "" : output.getKey() + File.separator) + config.profile + File.separator + output.getValue())
        ).collect(Collectors.toList());
        if (this.outputFiles.isEmpty()) {
            throw new GradleException("At least one output must be specified.");
        }
    }

    @TaskAction
    void build() {
        Project project = getProject();
        project.exec(spec -> {
            spec.commandLine(this.cargoCommand);
            spec.args(args);
            spec.workingDir(workingDir);
            spec.environment(environment);
        }).assertNormalExitValue();
    }

    @InputDirectory
    File getWorkingDir() {
        return this.workingDir;
    }

    @OutputFiles
    @SuppressWarnings("unused")
    List<File> getOutputFiles() {
        return this.outputFiles;
    }
}
