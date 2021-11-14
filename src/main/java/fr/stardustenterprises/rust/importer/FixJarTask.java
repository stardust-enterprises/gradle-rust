package fr.stardustenterprises.rust.importer;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.File;
import java.io.IOException;

public class FixJarTask extends DefaultTask {

    @TaskAction
    public void fixJar() {
        AbstractArchiveTask jarTask = (AbstractArchiveTask) getProject().getTasks().getByName("jar");
        File jarFile = jarTask.getArchiveFile().get().getAsFile();

        ZipFile zipFile = new ZipFile(jarFile);
        File cacheDir = new File(getProject().getBuildDir(), "rustImport");
        if(cacheDir.exists()) {
            try {
                FileUtils.deleteDirectory(cacheDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cacheDir.mkdirs();

        try {
            zipFile.extractAll(cacheDir.getAbsolutePath());
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
