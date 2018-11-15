package org.innovateuk.ifs.file.controller.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.file.resource.FileEntryResource;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_UP;

/**
 * Small view model holding attributes around files
 */
public class FileDetailsViewModel {

    public static final BigDecimal ONE_KB = BigDecimal.valueOf(1024L);

    private long formInputId;
    private String filename;
    private BigDecimal filesizeKbytes;

    public FileDetailsViewModel(FileEntryResource fileEntry) {
        this(fileEntry.getName(), fileEntry.getFilesizeBytes());
    }

    public FileDetailsViewModel(String filename, long filesizeBytes) {
        this.filename = filename;
        this.filesizeKbytes = BigDecimal.valueOf(filesizeBytes).divide(ONE_KB, 0, ROUND_UP);
    }

    public FileDetailsViewModel(long formInputId, String filename, long filesizeBytes) {
        this(filename, filesizeBytes);
        this.formInputId = formInputId;
    }

    public long getFormInputId() {
        return formInputId;
    }

    public String getFilename() {
        return filename;
    }

    public BigDecimal getFilesizeKbytes() {
        return filesizeKbytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FileDetailsViewModel that = (FileDetailsViewModel) o;

        return new EqualsBuilder()
                .append(formInputId, that.formInputId)
                .append(filename, that.filename)
                .append(filesizeKbytes, that.filesizeKbytes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(formInputId)
                .append(filename)
                .append(filesizeKbytes)
                .toHashCode();
    }
}
