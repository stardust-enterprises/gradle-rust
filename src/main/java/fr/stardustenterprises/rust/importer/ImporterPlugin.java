package fr.stardustenterprises.rust.importer;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.TaskContainer;

@NonNullApi
public class ImporterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        TaskContainer tasks = project.getTasks();
        ConfigurationContainer configurations = project.getConfigurations();

        Configuration configuration = configurations.create("rustImport");
        configuration.setCanBeConsumed(false);
        configuration.setCanBeResolved(true);

        ImporterTask importerTask = tasks.create("importRustResources", ImporterTask.class);
        importerTask.setGroup("rust");

        project.afterEvaluate(p -> {
            tasks.named("processResources").get().dependsOn(importerTask);
        });
    }
}
