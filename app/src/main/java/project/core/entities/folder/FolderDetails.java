package project.core.entities.folder;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class FolderDetails<T> {

    public String name = EMPTY;
    public String type = EMPTY;
    public String permissions = EMPTY;
    public T content;
    public String path = EMPTY;

    transient public FolderDetailsRules<T> rules = new FolderDetailsRules(this);

}
