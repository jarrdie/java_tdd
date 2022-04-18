package project.core.entities.permissions;

import static project.core.entities.permissions.FileType.UNDEFINED;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class BasicFilePermissions {

    public boolean areInitiated = false;
    public boolean isReadable = false;
    public boolean isWritable = false;
    public boolean isExecutable = false;
    public boolean isHidden = false;
    public FileType type = UNDEFINED;
    public String notation = EMPTY;

    transient public BasicFilePermissionsRules rules = new BasicFilePermissionsRules(this);

}
