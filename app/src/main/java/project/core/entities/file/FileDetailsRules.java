package project.core.entities.file;

import project.core.entities.permissions.BasicFilePermissions;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.logging.Logger;

import static java.nio.file.Files.probeContentType;
import static java.nio.file.Files.readAttributes;
import static java.time.ZoneId.systemDefault;
import static org.apache.commons.io.FileUtils.sizeOf;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.stripToEmpty;

public class FileDetailsRules {

    private static Logger LOG = Logger.getLogger(FileDetailsRules.class.getName());

    private static final String jsonExtension = "json";
    private static final String jsonMimeType = "application/json";
    private static final String pythonExtension = "py";
    private static final String pythonMimeType = "text/x-python";

    private FileDetails fileDetails;

    public FileDetailsRules(FileDetails fileDetails) {
        this.fileDetails = fileDetails;
    }

    public boolean areEmpty() {
        return isEmpty(fileDetails.name)
                || fileDetails.fileSize == -1
                || isEmpty(fileDetails.fileSizeUnit)
                || isEmpty(fileDetails.dates.lastModified);
    }

    public void setAllDetails(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        fileDetails.name = file.getName();
        fileDetails.mimeType = getMimeType(file);
        fileDetails.fileSize = getSize(file);
        fileDetails.fileSizeUnit = "bytes";
        fileDetails.permissions = getPermissions(file);
    }

    private String getMimeType(File file) {
        String defaultMimeType = EMPTY;
        if (file == null) {
            return defaultMimeType;
        }
        String fileName = file.getName();
        if (isEmpty(fileName)) {
            return defaultMimeType;
        }
        String extension = getExtension(fileName);
        extension = stripToEmpty(extension);
        try {
            if (extension.equalsIgnoreCase(jsonExtension)) {
                return jsonMimeType;
            }
            if (extension.equalsIgnoreCase(pythonExtension)) {
                return pythonMimeType;
            }
            return probeContentType(file.toPath());
        } catch (Exception ex) {
            LOG.severe("Could not determine the mime type of the file: " + file);
        }
        return defaultMimeType;
    }

    private long getSize(File file) {
        final long defaultSize = -1;
        if (file == null || !file.exists()) {
            return defaultSize;
        }
        try {
            return sizeOf(file);
        } catch (Exception ex) {
            LOG.severe("Could not determine the size of the file: " + file);
            return defaultSize;
        }
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
        fileDetails.path = file.getAbsolutePath();
    }

    public void addDates(File file, String dateTimeFormat) {
        if (file == null || !file.exists() || isEmpty(dateTimeFormat)) {
            return;
        }
        addDates(file);
        fileDetails.dates.lastModifiedLocal = getLastModifiedDateLocal(file, dateTimeFormat);
        fileDetails.dates.createdLocal = getCreationDateLocal(file, dateTimeFormat);
    }

    public void addDates(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        fileDetails.dates.lastModified = getLastModifiedDate(file);
        fileDetails.dates.created = getCreationDate(file);
    }

    private String getLastModifiedDate(File file) {
        String defaultDate = EMPTY;
        try {
            Instant instant = getLastModifiedInstant(file);
            return instant.toString();
        } catch (SecurityException exception) {
            LOG.severe(String.format("Could not determine the last modified date of the file [%s]", file));
            return defaultDate;
        }
    }

    private Instant getLastModifiedInstant(File file) {
        long milisFromEpoch = file.lastModified();
        Date date = new Date(milisFromEpoch);
        return date.toInstant();
    }

    private String getLastModifiedDateLocal(File file, String dateTimeFormat) {
        String defaultDate = EMPTY;
        try {
            Instant instant = getLastModifiedInstant(file);
            return toLocalFormat(instant, dateTimeFormat);
        } catch (SecurityException exception) {
            LOG.severe(String.format("Could not determine the last modified date of the file [%s]", file));
            return defaultDate;
        }
    }

    private String toLocalFormat(Instant instant, String dateTimeFormat) {
        LocalDateTime local = instant.atZone(systemDefault()).toLocalDateTime();
        local = local.truncatedTo(ChronoUnit.SECONDS);
        if (dateTimeFormat.contains("DD")) {
            dateTimeFormat = dateTimeFormat.replace("DD", "dd");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
        return local.format(formatter);
    }

    private String getCreationDate(File file) {
        String defaultDate = EMPTY;
        try {
            Instant instant = getCreationInstant(file);
            return instant.toString();
        } catch (Exception exception) {
            LOG.severe(String.format("Could not determine the creation date of the file [%s]", file));
            return defaultDate;
        }
    }

    private Instant getCreationInstant(File file) throws IOException {
        BasicFileAttributes attributes = readAttributes(file.toPath(), BasicFileAttributes.class);
        return attributes.creationTime().toInstant();
    }

    private String getCreationDateLocal(File file, String dateTimeFormat) {
        String defaultDate = EMPTY;
        try {
            Instant instant = getCreationInstant(file);
            return toLocalFormat(instant, dateTimeFormat);
        } catch (Exception exception) {
            LOG.severe(String.format("Could not determine the creation date of the file [%s]", file));
            return defaultDate;
        }
    }

}
