import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import com.ibm.jzos.FileAttribute;

@UtilityClass
class NativeLibraryUtils {

    /**
     * Collection of a loaded file to avoid loading the same library multiple times
     */
    private final Set<String> loaded = new HashSet<>();

    private File extract(String filename) {
        // create a copy of library in the temporary folder
        File libFile;
        try (InputStream is = NativeLibraryUtils.class.getResourceAsStream(filename)) {
            libFile = File.createTempFile(filename, ".so");
            try (OutputStream os = new FileOutputStream(libFile)) {
                IOUtils.copy(is, os);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException("Could not load native library `" + filename + "`", ioe);
        }

        // change file to be executable and program-controlled
        libFile.setExecutable(true);
        FileAttribute.setProgramControlled(libFile.getAbsolutePath(), true);

        // clean up file after application will be stopped
        libFile.deleteOnExit();

        return libFile;
    }

    private void load(String fileName) {
        // check if the file has not been loaded yet, if not load it
        synchronized (NativeLibraryUtils.class) {
            if (!loaded.contains(fileName)) {
                File file = extract(fileName);
                System.load(file.getAbsolutePath());
                loaded.add(fileName);
            }
        }
    }

    public void loadLibrary(String libraryName) {
        // construct name of file
        StringBuilder sb = new StringBuilder();
        sb.append("/lib/").append(libraryName);
        if ("32".equals(System.getProperty("com.ibm.vm.bitmode"))) {
            sb.append("-31");
        } else {
            sb.append("-64");
        }
        sb.append(".so");

        // load the library
        load(sb.toString());
    }

    // using filename
    static {
        System.load("/z/user/libfeature.so");
    }

    // using library name
    static {
        System.loadLibrary("feature");
    }

}