package project.core.entities.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static project.test.Constants.srcTestResourcesDir;

public class FileDetailsRulesTest {

    @Test
    public void testSetAllDetails() throws Exception {
        File file = new File(srcTestResourcesDir
                + "/references/02-case-files/TB5a-SiapMicros/s012-di DAK (ENG).pdf");
        FileDetails fileDetails = new FileDetails();
        fileDetails.rules.setAllDetails(file);
        fileDetails.rules.addDates(file, "YYYY-MM-DD HH:mm:ss");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(fileDetails);
        // System.out.println(json);
    }

    @Test
    public void testSetAllDetailsFileNotExists() throws Exception {
        File file = new File("nonexisting.xml");
        FileDetails fileDetails = new FileDetails();
        assertTrue(fileDetails.rules.areEmpty());
        fileDetails.rules.setAllDetails(file);
        assertTrue(fileDetails.rules.areEmpty());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(fileDetails);
        // System.out.println(json);
    }

    @Test
    public void testGetRelativeDirectory() {
    }

}
