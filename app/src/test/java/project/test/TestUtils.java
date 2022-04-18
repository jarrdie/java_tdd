package project.test;

import java.io.File;

import static project.test.Constants.srcTestResourcesDir;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;

public class TestUtils {

    public static String readTestFile(String testFile) throws Exception {
        File file = openTestFile(testFile);
        return readFileToString(file, UTF_8);
    }

    public static File openTestFile(String testFile) {
        return new File(srcTestResourcesDir + "/" + testFile);
    }

}
