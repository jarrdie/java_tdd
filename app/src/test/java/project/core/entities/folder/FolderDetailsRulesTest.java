package project.core.entities.folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import project.core.entities.file.FileDetails;
import java.io.File;
import org.junit.jupiter.api.*;

import static project.test.Constants.srcTestResourcesDir;

public class FolderDetailsRulesTest {

    private File folder;

    @BeforeEach
    public void setUp() {
        folder = new File(srcTestResourcesDir + "/references/30-edited");
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testInitiate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode content = mapper.createArrayNode();
        FolderDetails folderDetails = new FolderDetails();
        folderDetails.rules.initiate(folder, "empty", content);
        String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(folderDetails);
        // System.out.println(json);
    }

    @Test
    public void testInitiateMultiple() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ArrayNode c1 = mapper.createArrayNode();
        FolderDetails d1 = new FolderDetails();
        d1.rules.initiate(folder, "d1", c1);

        File file = new File(srcTestResourcesDir + "/references/30-edited/initial.json");
        FileDetails f1 = new FileDetails();
        f1.rules.setAllDetails(file);

        ObjectNode f1n = mapper.convertValue(f1, ObjectNode.class);
        c1.add(f1n);

        ArrayNode c2 = mapper.createArrayNode();
        FolderDetails d2 = new FolderDetails();
        d2.rules.initiate(folder, "d2", c2);

        ObjectNode d2n = mapper.convertValue(d2, ObjectNode.class);
        c1.add(d2n);

        String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(d1);
        // System.out.println(json);
    }

}
