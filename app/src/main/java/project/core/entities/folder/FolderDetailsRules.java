package project.core.entities.folder;

import project.core.entities.permissions.BasicFilePermissions;
import java.io.File;
import java.nio.file.Path;

public class FolderDetailsRules<T> {

    private static final String slash = "/";
    private static final String backSlash = "\\";

    private FolderDetails folderDetails;

    public FolderDetailsRules(FolderDetails folderDetails) {
        this.folderDetails = folderDetails;
    }

    public void initiate(File file, String name, T content) {
        folderDetails.name = name;
        folderDetails.type = "Folder";
        folderDetails.permissions = getPermissions(file);
        folderDetails.content = content;
    }

    public static String getRelativeDirectory(Path basePath, File directory) {
        final Path dirPath = directory.toPath();
        final String relativeDir = normalize(basePath.relativize(dirPath));
        return relativeDir;
    }

    private static String normalize(Path path) {
        String normalizedPath = path.normalize().toString().replace(backSlash, slash);
        if (normalizedPath.startsWith(slash)) {
            normalizedPath = normalizedPath.substring(1);
        }
        return normalizedPath;
    }

    private String getPermissions(File file) {
        BasicFilePermissions permissions = new BasicFilePermissions();
        permissions.rules.extractAll(file);
        return permissions.notation;
    }

    public void addPath(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        folderDetails.path = file.getAbsolutePath();
    }

}
