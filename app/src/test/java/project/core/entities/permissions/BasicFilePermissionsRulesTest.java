package project.core.entities.permissions;

import static project.core.entities.permissions.BasicFilePermissionsRules.setPermissions;
import java.io.File;
import org.junit.jupiter.api.*;

import static project.test.Constants.srcTestResourcesDir;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicFilePermissionsRulesTest {

    private BasicFilePermissions expected;
    private boolean check = true;
    private String fileName;
    private File file;
    private BasicFilePermissions actual;

    @BeforeEach
    public void setUp() throws Exception {
        expected = new BasicFilePermissions();
        actual = new BasicFilePermissions();
        expected.areInitiated = true;
        fileName = EMPTY;
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (!check) {
            return;
        }
        checkPermissions();
    }

    private void checkPermissions() {
        file = new File(srcTestResourcesDir + "/references/40-permissions/" + fileName);
        if (!file.exists()) {
            return;
        }
        actual.rules.extractAll(file);
        assertEquals(expected.areInitiated, actual.areInitiated);
        assertEquals(expected.isReadable, actual.isReadable);
        assertEquals(expected.isWritable, actual.isWritable);
        assertEquals(expected.isExecutable, actual.isExecutable);
        assertEquals(expected.isHidden, actual.isHidden);
        assertEquals(expected.notation, actual.notation);
    }

    @Test
    public void testExtractEmpty() {
        actual.rules.extractAll(null);
        assert (true);
        actual.rules.extractAll(new File("nonexisting"));
        assert (true);
    }

    @Test
    public void testExtractAllPermissions() {
        fileName = "windows-read-write.json";
        expected.isReadable = true;
        expected.isWritable = true;
        expected.isExecutable = true;
        expected.isHidden = false;
        expected.notation = "-f-rwx-";
    }

    @Test
    public void testExtractAllPermissionsReadOnly() {
        fileName = "windows-read-only.json";
        expected.isReadable = true;
        expected.isWritable = false;
        expected.isExecutable = true;
        expected.isHidden = false;
        expected.notation = "-f-r-x-";
    }

    @Test
    public void testExtractAllPermissionsHidden() {
        fileName = "windows-hidden.json";
        expected.isReadable = false;
        expected.isWritable = false;
        expected.isExecutable = false;
        expected.isHidden = true;
        expected.notation = "-f----h";
    }

    @Test
    public void testExtractAllPermissionsDirectory() {
        fileName = "windows-read-write";
        expected.isReadable = true;
        expected.isWritable = true;
        expected.isExecutable = true;
        expected.isHidden = false;
        expected.notation = "d--rwx-";
    }

    @Test
    /*
     * Windows does not have read only directories as such, what it does
     * actually is to convert the internal files into read-only
     */
    public void testExtractAllPermissionsReadOnlyDirectory() {
        fileName = "windows-read-only";
        expected.isReadable = true;
        expected.isWritable = true;
        expected.isExecutable = true;
        expected.isHidden = false;
        expected.notation = "d--rwx-";
    }

    @Test
    /*
     * From the Api:
     * On UNIX for example a file is considered to be hidden if its name begins
     * with a period character ('.'). On Windows a file is considered hidden if
     * it isn't a directory and the DOS hidden attribute is set.
     */
    public void testExtractAllPermissionsHiddenDirectory() {
        fileName = "windows-hidden";
        expected.isReadable = true;
        expected.isWritable = true;
        expected.isExecutable = true;
        expected.isHidden = false;
        expected.notation = "d--rwx-";
    }

    @Test
    public void testSetPermissionsEmpty() {
        setPermissions(null, null);
        setPermissions(null, "rwxrwxrwx");
        setPermissions(new File("nonexisting"), "rwxrwxrwx");
    }

    @Test
    public void testSetPermissions() {
        check = false;
        file = new File(srcTestResourcesDir + "/references/70-scripts/script.sh");
        actual.rules.extractAll(file);
        assertEquals("-f-rw--", actual.notation);

        setPermissions(file, "rwxrwxrwx");
        actual.rules.extractAll(file);
        assertEquals("-f-rwx-", actual.notation);

        setPermissions(file, "rw-rw-rw-");
    }

}
