package project.core.entities.permissions;

import java.io.File;
import org.junit.jupiter.api.*;

import static project.core.entities.permissions.FileType.DIRECTORY;
import static project.core.entities.permissions.FileType.REGULAR_FILE;
import static project.core.entities.permissions.FileType.SYMBOLIC_LINK;
import static project.core.entities.permissions.FileType.UNDEFINED;
import static project.test.Constants.srcTestResourcesDir;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileTypeTest {

    private BasicFilePermissions permissions;
    private String testPath;
    private FileType expectedType;

    @BeforeEach
    public void setUp() throws Exception {
        permissions = new BasicFilePermissions();
        testPath = EMPTY;
        expectedType = UNDEFINED;
    }

    private void checkType() {
        File file = new File(srcTestResourcesDir + "/references/03-file-types/" + testPath);
        if (!file.exists()) {
            return;
        }
        permissions.rules.extractAll(file);
        assertEquals(expectedType, permissions.type);
    }

    @Test
    public void testFileType() {
        testPath = "directory";
        expectedType = DIRECTORY;
        checkType();

        testPath = "file.txt";
        expectedType = REGULAR_FILE;
        checkType();
    }

    @Test
    public void testFileTypeLinks() {
        testPath = "directoryJunction";
        expectedType = DIRECTORY;
        checkType();

        testPath = "directory-Shortcut.lnk";
        expectedType = REGULAR_FILE;
        checkType();

        testPath = "file.txt-Shortcut.lnk";
        expectedType = REGULAR_FILE;
        checkType();

        testPath = "directoryLink";
        expectedType = SYMBOLIC_LINK;
        checkType();

        testPath = "fileLink.txt";
        expectedType = SYMBOLIC_LINK;
        checkType();
    }

}
