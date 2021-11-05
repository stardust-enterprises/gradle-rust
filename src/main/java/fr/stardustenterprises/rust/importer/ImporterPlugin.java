package fr.stardustenterprises.rust.importer;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.plugins.PluginManager;

@NonNullApi
public class ImporterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        PluginManager pluginManager = project.getPluginManager();
        ConfigurationContainer configurations = project.getConfigurations();

        Configuration implementation = null;

        try {
            implementation = configurations.getByName("implementation");
        } catch (UnknownConfigurationException ignored) {
        }

        boolean hasJavaPlugin = pluginManager.hasPlugin("java") || pluginManager.hasPlugin("java-library");
        if (implementation == null) {
            if(hasJavaPlugin) {
                throw new RuntimeException("what.");
            }

            pluginManager.apply("java");
        }

        Configuration configuration = configurations.create("rustImport");
        configuration.extendsFrom(implementation);
    }
}
