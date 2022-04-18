package project.core.usecases.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GetFile {

    public InputStream getFile(File file) throws FileNotFoundException {
        FileInputStream input = new FileInputStream(file);
        return input;
    }

}
