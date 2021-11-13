package fr.stardustenterprises.rust.importer;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.io.File;

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

        project.afterEvaluate(__ -> {
            tasks.named("processResources").get().dependsOn(importerTask);
        });
    }
}
