package project.core.usecases.directory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.util.logging.Logger;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

public class DeleteDirectory {

    private static Logger LOG = Logger.getLogger(DeleteDirectory.class.getName());

    private ObjectMapper mapper;

    public DeleteDirectory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode deleteDirectory(String basePath, String path)
            throws IOException {
        basePath = stripToEmpty(basePath);
        path = stripToEmpty(path);
        File file = getFile(basePath, path);
        LOG.info("Directory to delete: " + file.getAbsolutePath());
        if (!file.exists()) {
            String message = "Not removed, the directory does not exist: " + file.getAbsolutePath();
            LOG.severe(message);
            throw new IOException(message);
        }
        FileUtils.deleteDirectory(file);
        ObjectNode node = mapper.createObjectNode();
        node.put("result", "ok");
        return node;
    }

}
