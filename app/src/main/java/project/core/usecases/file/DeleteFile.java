package project.core.usecases.file;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DeleteFile {

    private static Logger LOG = Logger.getLogger(DeleteFile.class.getName());

    private ObjectMapper mapper;

    public DeleteFile(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode deleteFile(String basePath, String path)
            throws IOException {
        basePath = stripToEmpty(basePath);
        path = stripToEmpty(path);
        File file = getFile(basePath, path);
        LOG.info("File to delete: " + file.getAbsolutePath());
        if (!file.exists()) {
            String message = "Not deleted, the file does not exists: " + file.getAbsolutePath();
            LOG.severe(message);
            throw new IOException(message);
        }
        Files.delete(file.toPath());
        ObjectNode node = mapper.createObjectNode();
        node.put("result", "ok");
        return node;
    }

}
