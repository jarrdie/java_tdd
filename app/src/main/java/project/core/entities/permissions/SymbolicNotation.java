package project.core.entities.permissions;

import static project.core.entities.permissions.FileType.DIRECTORY;
import static project.core.entities.permissions.FileType.REGULAR_FILE;
import static project.core.entities.permissions.FileType.SYMBOLIC_LINK;

public class SymbolicNotation {

    class Flags {

        public String directory = "-";
        public String file = "-";
        public String link = "-";
        public String read = "-";
        public String write = "-";
        public String execute = "-";
        public String hidden = "-";
    }

    private BasicFilePermissions permissions;
    private Flags flags;

    public SymbolicNotation(BasicFilePermissions permissions) {
        this.permissions = permissions;
        this.flags = new Flags();
    }

    public String getNotation() {
        String defaultNotation = createNotation();
        if (permissions == null || !permissions.areInitiated) {
            return defaultNotation;
        }
        if (permissions.type == DIRECTORY) {
            flags.directory = "d";
        }
        if (permissions.type == REGULAR_FILE) {
            flags.file = "f";
        }
        if (permissions.type == SYMBOLIC_LINK) {
            flags.link = "l";
        }
        if (permissions.isReadable) {
            flags.read = "r";
        }
        if (permissions.isWritable) {
            flags.write = "w";
        }
        if (permissions.isExecutable) {
            flags.execute = "x";
        }
        if (permissions.isHidden) {
            flags.hidden = "h";
        }
        return createNotation();
    }

    private String createNotation() {
        return flags.directory + flags.file + flags.link
                + flags.read + flags.write + flags.execute
                + flags.hidden;
    }

}
