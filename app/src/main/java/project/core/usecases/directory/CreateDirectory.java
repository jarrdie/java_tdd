package project.core.usecases.directory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

public class CreateDirectory {
    private static Logger LOG = Logger.getLogger(CreateDirectory.class.getName());

    private ObjectMapper mapper;

    public CreateDirectory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode createDirectory(String basePath, String path)
            throws IOException {
        basePath = stripToEmpty(basePath);
        path = stripToEmpty(path);
        File file = getFile(basePath, path);
        LOG.info("Directory to create: " + file.getAbsolutePath());
        if (file.exists()) {
            String message = "Not created, the directory already exists: " + file.getAbsolutePath();
            LOG.severe(message);
            throw new IOException(message);
        }
        forceMkdir(file);
        ObjectNode node = mapper.createObjectNode();
        node.put("result", "ok");
        return node;
    }

}
