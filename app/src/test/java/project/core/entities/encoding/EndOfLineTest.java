package project.core.entities.encoding;

import static org.apache.commons.io.FileUtils.copyFile;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static project.core.entities.encoding.EndOfLine.readAsUtf8;
import static project.core.entities.encoding.EndOfLine.unifyLineBreaksToUnix;
import static project.test.Constants.srcTestResourcesDir;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EndOfLineTest {

    private File windowsFile;
    private File macFile;
    private File unixFile;
    private File eolFile;
    private String content;

    @BeforeEach
    public void setUp() {
        windowsFile = new File(srcTestResourcesDir + "/references/70-scripts/eol_windows.sh");
        macFile = new File(srcTestResourcesDir + "/references/70-scripts/eol_mac.sh");
        unixFile = new File(srcTestResourcesDir + "/references/70-scripts/eol_unix.sh");
        eolFile = new File(srcTestResourcesDir + "/references/70-scripts/eol.sh");
    }

    private void check() throws Exception {
        content = readAsUtf8(eolFile);
        assertFalse(content.contains("\r\n"));
        assertTrue(content.contains("\n"));
    }

    @Test
    public void testUnifyLineBreaksToUnix() throws Exception {
        copyFile(windowsFile, eolFile);
        unifyLineBreaksToUnix(eolFile);
        check();

        copyFile(macFile, eolFile);
        unifyLineBreaksToUnix(eolFile);
        check();

        copyFile(unixFile, eolFile);
        unifyLineBreaksToUnix(eolFile);
        check();
    }

}
