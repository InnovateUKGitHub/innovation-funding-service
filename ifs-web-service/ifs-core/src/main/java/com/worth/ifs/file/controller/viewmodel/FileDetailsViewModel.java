package com.worth.ifs.file.controller.viewmodel;

import com.worth.ifs.file.resource.FileEntryResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_EVEN;

/**
 * Small view model holding attributes around files
 */
public class FileDetailsViewModel {

    public static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000L);

    private String filename;
    private BigDecimal filesizeKbytes;

    public FileDetailsViewModel(FileEntryResource fileEntry) {
        this(fileEntry.getName(), fileEntry.getFilesizeBytes());
    }

    public FileDetailsViewModel(String filename, long filesizeBytes) {
        this.filename = filename;
        this.filesizeKbytes = BigDecimal.valueOf(filesizeBytes).divide(ONE_THOUSAND, 2, ROUND_HALF_EVEN);
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
                .append(filename, that.filename)
                .append(filesizeKbytes, that.filesizeKbytes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(filename)
                .append(filesizeKbytes)
                .toHashCode();
    }
}
