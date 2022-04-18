package project.core.entities.file;

import project.core.entities.dates.FileDates;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class FileDetails {

    public String name = EMPTY;
    public String mimeType = EMPTY;
    public long fileSize = -1;
    public String fileSizeUnit = EMPTY;
    public String permissions = EMPTY;
    public String path = EMPTY;
    public FileDates dates = new FileDates();

    transient public FileDetailsRules rules = new FileDetailsRules(this);

}
