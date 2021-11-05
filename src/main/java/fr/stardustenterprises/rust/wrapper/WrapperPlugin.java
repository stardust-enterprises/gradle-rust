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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

/**
 * A plugin that wraps Rust's build systems,
 * for embedding Rust libraries in Java projects.
 */
@SuppressWarnings("unused")
public class WrapperPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        project.getConfigurations().create("default");

        WrapperExtension extension = project.getExtensions().create("rust", WrapperExtension.class);

        TaskProvider<WrapperTask> wrapperTask = project.getTasks().register("build", WrapperTask.class);
        project.afterEvaluate(__ -> {
            wrapperTask.get().configure(extension);
            project.getArtifacts().add("default", wrapperTask);
        });
    }
}
