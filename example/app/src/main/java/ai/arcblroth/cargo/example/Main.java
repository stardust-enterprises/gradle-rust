package ai.arcblroth.cargo.example;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Main {
    static {
        // invalid code
        // todo: fix
        try {
            // Extract and load our native library
            String nativeLibraryName = System.mapLibraryName("wrapper_example");
            File tempFile = File.createTempFile("extracted_", nativeLibraryName);
            try(ReadableByteChannel src = Channels.newChannel(Main.class.getClassLoader().getResourceAsStream(nativeLibraryName));
                FileChannel dst = new FileOutputStream(tempFile).getChannel()) {
                dst.transferFrom(src, 0, Long.MAX_VALUE);
            }
            System.load(tempFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        doStuff();
    }

    // Implemented by our native library in
    // the :native project.
    private native static void doStuff();
}
