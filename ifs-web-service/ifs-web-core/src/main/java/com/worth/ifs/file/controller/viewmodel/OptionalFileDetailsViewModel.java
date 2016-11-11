package com.worth.ifs.file.controller.viewmodel;

import com.worth.ifs.file.resource.FileEntryResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

/**
 * Holder of model attributes around the uploaded Assessor Feedback
 */
public class OptionalFileDetailsViewModel {

    private FileDetailsViewModel fileDetails;
    private boolean readonly;

    private OptionalFileDetailsViewModel(boolean readonly, String filename, Long filesizeBytes) {
        this(readonly, new FileDetailsViewModel(filename, filesizeBytes));
    }

    private OptionalFileDetailsViewModel(boolean readonly, FileDetailsViewModel fileDetails) {
        this.readonly = readonly;
        this.fileDetails = fileDetails;
    }

    public static OptionalFileDetailsViewModel withExistingFile(FileEntryResource fileEntry, boolean readonly) {
        return withExistingFile(fileEntry.getName(), fileEntry.getFilesizeBytes(), readonly);
    }

    public static OptionalFileDetailsViewModel withExistingFile(String filename, long filesizeBytes, boolean readonly) {
        return new OptionalFileDetailsViewModel(readonly, filename, filesizeBytes);
    }

    public static OptionalFileDetailsViewModel withNoFile(boolean readonly) {
        return new OptionalFileDetailsViewModel(readonly, null);
    }

    public boolean isReadonly() {
        return readonly;
    }

    public boolean isFileUploaded() {
        return fileDetails != null;
    }

    public String getFilename() {
        return fileDetails != null ? fileDetails.getFilename() : null;
    }

    public BigDecimal getFilesizeKbytes() {
        return fileDetails != null ? fileDetails.getFilesizeKbytes() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OptionalFileDetailsViewModel that = (OptionalFileDetailsViewModel) o;

        return new EqualsBuilder()
                .append(readonly, that.readonly)
                .append(fileDetails, that.fileDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fileDetails)
                .append(readonly)
                .toHashCode();
    }
}
