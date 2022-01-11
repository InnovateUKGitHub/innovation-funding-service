package org.innovateuk.ifs.file.controller.viewmodel;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_UP;

/**
 * Small view model holding attributes around files
 */
@EqualsAndHashCode
@ToString
public class FileDetailsViewModel {

    public static final BigDecimal ONE_KB = BigDecimal.valueOf(1024L);

    private long formInputId;
    private long fileEntryId;
    private String filename;
    private BigDecimal filesizeKbytes;

    public FileDetailsViewModel(String filename, long filesizeBytes) {
        this.filename = filename;
        this.filesizeKbytes = BigDecimal.valueOf(filesizeBytes).divide(ONE_KB, 0, ROUND_UP);
    }

    public FileDetailsViewModel(String filename, long fileEntryId, long filesizeBytes) {
        this(filename, filesizeBytes);
        this.fileEntryId = fileEntryId;
    }

    public FileDetailsViewModel(FileEntryResource fileEntry) {
        this(fileEntry.getName(), fileEntry.getId(), fileEntry.getFilesizeBytes());
    }

    public FileDetailsViewModel(long formInputId, long fileEntryId, String filename, long filesizeBytes) {
        this(filename, fileEntryId, filesizeBytes);
        this.formInputId = formInputId;
    }

    public long getFormInputId() {
        return formInputId;
    }

    public long getFileEntryId() {
        return fileEntryId;
    }

    public String getFilename() {
        return filename;
    }

    public BigDecimal getFilesizeKbytes() {
        return filesizeKbytes;
    }

}
