package project.core.entities.folder;

import project.test.EntityTestBase;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FolderDetailsTest extends EntityTestBase<FolderDetails, FolderDetailsTest> {

    @Test
    public void testEntity() throws Exception {
        test = this;
        entityClass = FolderDetails.class;
        validate();
    }

    public void checkEmpty() throws Exception {
        assertEquals("empty", currentEntity.name);
        assertEquals("Folder", currentEntity.type);
        assertEquals("d--rwx-", currentEntity.permissions);
        assertNotNull(currentEntity.content);
        setArray(currentEntity.content);
        assertEquals(0, array.size());
    }

    public void checkWithContent() throws Exception {
        assertEquals("d1", currentEntity.name);
        assertEquals("Folder", currentEntity.type);
        assertEquals("d--rwx-", currentEntity.permissions);
        assertNotNull(currentEntity.content);
        setArray(currentEntity.content);
        assertEquals(2, array.size());

        setMap(array.get(0));
        assertEquals("initial.json", map.get("name"));
        assertEquals("application/json", map.get("mimeType"));
        assertEquals(28, map.get("fileSize"));
        assertEquals("bytes", map.get("fileSizeUnit"));
        assertEquals("2018-11-15T09:27:24.797Z", map.get("lastModified"));
        assertEquals("-f-rwx-", map.get("permissions"));

        setMap(array.get(1));
        assertEquals("d2", map.get("name"));
        assertEquals("Folder", map.get("type"));
        assertEquals("d--rwx-", map.get("permissions"));
        setArray(map.get("content"));
        assertEquals(0, array.size());
    }

}
