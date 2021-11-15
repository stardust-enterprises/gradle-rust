package fr.stardustenterprises.rust.importer;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FixJarTask extends DefaultTask {

    @TaskAction
    public void fixJar() throws IOException {
        AbstractArchiveTask jarTask = (AbstractArchiveTask) getProject().getTasks().getByName("jar");
        File jarFile = jarTask.getArchiveFile().get().getAsFile();

        ZipFile zipFile = new ZipFile(jarFile);
        File cacheDir = new File(getProject().getBuildDir(), "rustImport");
        if (cacheDir.exists()) {
            FileUtils.deleteDirectory(cacheDir);
        }
        cacheDir.mkdirs();

        File javaExtract = new File(cacheDir, "java");
        javaExtract.mkdir();

        zipFile.extractAll(javaExtract.getAbsolutePath());

        File exportZip = new File(javaExtract, "export.zip");
        if (!exportZip.exists()) {
            throw new RuntimeException("Export zip doesn't exist!");
        }

        File nativeExtract = new File(cacheDir, "rust");
        nativeExtract.mkdir();

        ZipFile exportZipFile = new ZipFile(exportZip);
        exportZipFile.extractAll(nativeExtract.getAbsolutePath());

        exportZip.delete();

        File[] files = nativeExtract.listFiles();
        assert files != null;

        for (File file : files) {
            File dest = new File(javaExtract, file.getName());
            if (file.isDirectory()) {
                FileUtils.copyDirectory(file, dest);
            } else {
                FileUtils.copyFile(file, dest);
            }
        }

        jarFile.delete();
        ZipFile finalZip = new ZipFile(jarFile);
        for (File f : Objects.requireNonNull(javaExtract.listFiles())) {
            if (f.isFile()) {
                finalZip.addFile(f);
            } else {
                finalZip.addFolder(f);
            }
        }
    }
}
