package project.core.entities.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import static project.core.entities.permissions.FileType.DIRECTORY;
import static project.core.entities.permissions.FileType.REGULAR_FILE;
import static project.core.entities.permissions.FileType.SYMBOLIC_LINK;
import static project.core.entities.permissions.FileType.UNDEFINED;
import static java.nio.file.Files.isExecutable;
import static java.nio.file.Files.isReadable;
import static java.nio.file.Files.isWritable;
import static java.nio.file.Files.setPosixFilePermissions;
import static java.nio.file.attribute.PosixFilePermissions.fromString;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class BasicFilePermissionsRules {

    public static Logger LOG = Logger.getLogger(BasicFilePermissionsRules.class.getName());

    private BasicFilePermissions permissions;

    public BasicFilePermissionsRules(BasicFilePermissions permissions) {
        this.permissions = permissions;
    }

    public void extractAll(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        Path path = file.toPath();
        permissions.areInitiated = true;
        permissions.isReadable = isReadable(path);
        permissions.isWritable = isWritable(path);
        permissions.isExecutable = isExecutable(path);
        permissions.isHidden = isHidden(path);
        permissions.type = getType(file);
        SymbolicNotation notation = new SymbolicNotation(permissions);
        permissions.notation = notation.getNotation();
    }

    private boolean isHidden(Path path) {
        try {
            return Files.isHidden(path);
        } catch (IOException ex) {
            return false;
        }
    }

    private FileType getType(File file) {
        Path path = file.toPath();
        // The order matters
        if (Files.isSymbolicLink(path)) {
            return SYMBOLIC_LINK;
        }
        if (Files.isDirectory(path)) {
            return DIRECTORY;
        }
        if (Files.isRegularFile(path)) {
            return REGULAR_FILE;
        }
        return UNDEFINED;
    }

    public static void setPermissions(File file, String permissions) {
        if (isEmpty(permissions) || file == null || !file.exists()) {
            return;
        }
        try {
            Path path = file.toPath();
            setPosixFilePermissions(path, fromString(permissions));
        } catch (Exception ex) {
            LOG.info("Permissions " + permissions + " not set on file " + "due to: " + ex.getMessage());
        }
    }

}
