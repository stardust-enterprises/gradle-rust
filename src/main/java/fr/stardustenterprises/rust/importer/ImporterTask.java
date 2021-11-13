package fr.stardustenterprises.rust.importer;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImporterTask extends DefaultTask {

    @TaskAction
    public void copy() {
        Project project = getProject();
        ConfigurationContainer configurations = project.getConfigurations();

        File resourcesDir = new File(project.getBuildDir(), "resources" + File.separator + "main");
        resourcesDir.mkdirs();

        List<File> imports = new ArrayList<>();
        configurations.getByName("rustImport").forEach(imports::add);

        imports.forEach(file -> {
            ZipFile zipFile = new ZipFile(file);
            try {
                zipFile.extractAll(resourcesDir.getAbsolutePath());
            } catch (ZipException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
