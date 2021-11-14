package fr.stardustenterprises.rust.importer;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.language.jvm.tasks.ProcessResources;

@NonNullApi
public class ImporterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        TaskContainer tasks = project.getTasks();
        ConfigurationContainer configurations = project.getConfigurations();

        Configuration configuration = configurations.create("rustImport");
        configuration.setCanBeConsumed(false);
        configuration.setCanBeResolved(true);

        FixJarTask task = tasks.create("fixImport", FixJarTask.class);
        task.setGroup("rust-import");

        project.afterEvaluate(p -> {
            tasks.withType(ProcessResources.class).named("processResources").configure(it -> {
                it.from(configuration);
            });
            tasks.named("build").get().dependsOn(task);
        });
    }
}
