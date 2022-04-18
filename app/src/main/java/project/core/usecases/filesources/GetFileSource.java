package project.core.usecases.filesources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import project.core.entities.file.FileDetails;
import project.core.entities.folder.FolderDetails;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.comparator.NameFileComparator;
import java.util.logging.Logger;

import static project.core.entities.folder.FolderDetailsRules.getRelativeDirectory;

public class GetFileSource extends DirectoryWalker {

    private static Logger LOG = Logger.getLogger(GetFileSource.class.getName());
    private static final String dataKey = "data";
    private static final int endDepth = 0;

    private Path basePath;
    private int depth;
    private ObjectNode response;
    protected ObjectMapper mapper;
    protected Map<String, ObjectNode> sortedFolderMap;
    protected Map<String, ObjectNode> sortedFileMap;

    public GetFileSource(int defaultDepth, ObjectMapper mapper) {
        super(null, null, defaultDepth);
        this.mapper = mapper;
        response = mapper.createObjectNode();
        this.sortedFolderMap = new TreeMap<>();
        this.sortedFileMap = new TreeMap<>();
    }

    public ObjectNode getFileSource(File requestDirectory, Integer requestDepth)
            throws IOException {
        int depthLimit = 100;
        basePath = requestDirectory.toPath();
        depth = requestDepth;
        if (requestDepth > depthLimit) {
            depth = depthLimit;
            LOG.info("The requested depth was to large: " + requestDepth
                    + ". Using " + depthLimit);
        }
        response.put("name", requestDirectory.getName());
        Date date = new Date(); // UTC
        response.put("creationDateTime", date.toInstant().toString());
        response.set(dataKey, mapper.createArrayNode());
        walk(requestDirectory, null);
        addDataToResponse();
        return response;
    }

    private void addDataToResponse() {
        ArrayNode data = mapper.createArrayNode();
        for (Map.Entry<String, ObjectNode> entry : this.sortedFolderMap.entrySet()) {
            ObjectNode node = entry.getValue();
            data.add(node);
        }
        for (Map.Entry<String, ObjectNode> entry : this.sortedFileMap.entrySet()) {
            ObjectNode node = entry.getValue();
            data.add(node);
        }
        response.set(dataKey, data);
    }

    @Override
    protected void handleFile(final File file, final int currentDepth,
            final Collection results) throws IOException {
        FileDetails fileDetails = createFileDetails(file);
        ObjectNode jsonFile = mapper.convertValue(fileDetails, ObjectNode.class);
        addFileToMap(fileDetails, jsonFile);
    }

    @Override
    protected void handleDirectoryEnd(File directory, int currentDepth,
            Collection results) throws IOException {
        if (currentDepth == endDepth) {
            return;
        }
        String relativeDirectory = getRelativeDirectory(basePath, directory);
        FolderDetails<ArrayNode> folderDetails = createFolderDetails(directory, relativeDirectory);
        listFiles(directory, this.depth, folderDetails.content);
        ObjectNode jsonFolder = mapper.convertValue(folderDetails, ObjectNode.class);
        addFolderToMap(folderDetails, jsonFolder);
    }

    private void listFiles(File directory, int currentDepth, ArrayNode output) {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        if (currentDepth <= 0) {
            return;
        }
        currentDepth--;
        File[] files = directory.listFiles();
        Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);
        for (File file : files) {
            createDetails(file, currentDepth, output);
        }
    }

    private void createDetails(File file, int currentDepth, ArrayNode output) {
        if (file.isFile()) {
            FileDetails fileDetails = createFileDetails(file);
            ObjectNode jsonFile = mapper.convertValue(fileDetails, ObjectNode.class);
            output.add(jsonFile);
            return;
        }
        if (file.isDirectory()) {
            FolderDetails<ArrayNode> folderDetails = createFolderDetails(file, file.getName());
            listFiles(file, currentDepth, folderDetails.content);
            ObjectNode jsonFolder = mapper.convertValue(folderDetails, ObjectNode.class);
            output.add(jsonFolder);
        }
    }

    protected FolderDetails createFolderDetails(File file, String name) {
        ArrayNode content = mapper.createArrayNode();
        FolderDetails<ArrayNode> folderDetails = new FolderDetails<>();
        folderDetails.rules.initiate(file, name, content);
        return folderDetails;
    }

    protected FileDetails createFileDetails(File file) {
        FileDetails fileDetails = new FileDetails();
        fileDetails.rules.setAllDetails(file);
        fileDetails.rules.addDates(file);
        return fileDetails;
    }

    protected void addFolderToMap(FolderDetails<ArrayNode> folderDetails, ObjectNode jsonFile) {
        this.sortedFolderMap.put(folderDetails.name, jsonFile);
    }

    protected void addFileToMap(FileDetails fileDetails, ObjectNode jsonFile) {
        this.sortedFileMap.put(fileDetails.name, jsonFile);
    }

}
