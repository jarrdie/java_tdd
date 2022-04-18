package project.core.entities.permissions;

import org.junit.jupiter.api.*;

import static project.core.entities.permissions.FileType.DIRECTORY;
import static project.core.entities.permissions.FileType.REGULAR_FILE;
import static project.core.entities.permissions.FileType.SYMBOLIC_LINK;
import static org.junit.jupiter.api.Assertions.*;

public class SymbolicNotationTest {

    private BasicFilePermissions permissions;
    private SymbolicNotation notation;

    @BeforeEach
    public void setUp() {
        permissions = new BasicFilePermissions();
        notation = new SymbolicNotation(permissions);
        permissions.areInitiated = true;
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetNotationNull() {
        notation = new SymbolicNotation(null);
        assertEquals("-------", notation.getNotation());
    }

    @Test
    public void testGetNotationNotInitiated() {
        permissions.areInitiated = false;
        assertEquals("-------", notation.getNotation());
    }

    @Test
    public void testGetNotation() {
        assertEquals("-------", notation.getNotation());
    }

    @Test
    public void testGetNotationOnlyDirectory() {
        permissions.type = DIRECTORY;
        assertEquals("d------", notation.getNotation());
    }

    @Test
    public void testGetNotationOnlyFile() {
        permissions.type = REGULAR_FILE;
        assertEquals("-f-----", notation.getNotation());
    }

    @Test
    public void testGetNotationOnlySymbolicLink() {
        permissions.type = SYMBOLIC_LINK;
        assertEquals("--l----", notation.getNotation());
    }

    @Test
    public void testGetNotationOnlyRead() {
        permissions.isReadable = true;
        assertEquals("---r---", notation.getNotation());
    }

    @Test
    public void testGetNotationOnlyWrite() {
        permissions.isWritable = true;
        assertEquals("----w--", notation.getNotation());
    }

    @Test
    public void testGetNotationOnlyExecute() {
        permissions.isExecutable = true;
        assertEquals("-----x-", notation.getNotation());
    }

    @Test
    public void testGetNotationOnlyHidden() {
        permissions.isHidden = true;
        assertEquals("------h", notation.getNotation());
    }

    @Test
    public void testGetNotationNormalFile() {
        permissions.type = REGULAR_FILE;
        permissions.isReadable = true;
        permissions.isWritable = true;
        assertEquals("-f-rw--", notation.getNotation());
    }

    @Test
    public void testGetNotationExecutableFile() {
        permissions.type = REGULAR_FILE;
        permissions.isReadable = true;
        permissions.isWritable = true;
        permissions.isExecutable = true;
        assertEquals("-f-rwx-", notation.getNotation());
    }

    @Test
    public void testGetNotationReadOnlyFile() {
        permissions.type = REGULAR_FILE;
        permissions.isReadable = true;
        assertEquals("-f-r---", notation.getNotation());
    }

    @Test
    public void testGetNotationWriteOnlyFile() {
        permissions.type = REGULAR_FILE;
        permissions.isWritable = true;
        assertEquals("-f--w--", notation.getNotation());
    }

    @Test
    public void testGetNotationHiddenFile() {
        permissions.type = REGULAR_FILE;
        permissions.isHidden = true;
        assertEquals("-f----h", notation.getNotation());
    }

    @Test
    public void testGetNotationNormalDirectory() {
        permissions.type = DIRECTORY;
        permissions.isReadable = true;
        permissions.isWritable = true;
        permissions.isExecutable = true;
        assertEquals("d--rwx-", notation.getNotation());
    }

}
