package project.core.entities.file;

import project.test.EntityTestBase;
import org.junit.jupiter.api.*;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileDetailsTest extends EntityTestBase<FileDetails, FileDetailsTest> {

    @Test
    public void testEntity() throws Exception {
        test = this;
        entityClass = FileDetails.class;
        validate();
    }

    public void checkEmpty() throws Exception {
        assertEquals(EMPTY, currentEntity.name);
        assertEquals(EMPTY, currentEntity.mimeType);
        assertEquals(-1, currentEntity.fileSize);
        assertEquals(EMPTY, currentEntity.fileSizeUnit);
        assertEquals(EMPTY, currentEntity.dates.lastModified);
        assertEquals(EMPTY, currentEntity.permissions);
    }

    public void checkJson() throws Exception {
        assertEquals("initial.json", currentEntity.name);
        assertEquals("application/json", currentEntity.mimeType);
        assertEquals(28, currentEntity.fileSize);
        assertEquals("bytes", currentEntity.fileSizeUnit);
        assertEquals("2018-11-15T09:27:24.797Z", currentEntity.dates.lastModified);
        assertEquals("-f-rwx-", currentEntity.permissions);
    }

    public void checkPdf() throws Exception {
        assertEquals("s012-di DAK (ENG).pdf", currentEntity.name);
        assertEquals("application/pdf", currentEntity.mimeType);
        assertEquals(765257, currentEntity.fileSize);
        assertEquals("bytes", currentEntity.fileSizeUnit);
        assertEquals("2018-10-18T12:46:26Z", currentEntity.dates.lastModified);
        assertEquals("-f-rwx-", currentEntity.permissions);
    }

    public void checkXml() throws Exception {
        assertEquals("12 CAM NHUONG_0818_INI.xml", currentEntity.name);
        assertEquals("text/xml", currentEntity.mimeType);
        assertEquals(826, currentEntity.fileSize);
        assertEquals("bytes", currentEntity.fileSizeUnit);
        assertEquals("2018-10-18T12:46:16Z", currentEntity.dates.lastModified);
        assertEquals("-f-rwx-", currentEntity.permissions);
    }

}
